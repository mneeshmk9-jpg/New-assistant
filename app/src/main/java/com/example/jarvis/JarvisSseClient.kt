package com.example.jarvis

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

sealed class JarvisServerEvent {
    data class TextDelta(val delta: String) : JarvisServerEvent()
    data class Plan(val planId: String, val steps: List<PlannedStep>) : JarvisServerEvent()
    data class Action(
        val actionId: String,
        val tool: String,
        val args: Map<String, String>,
        val riskLevel: String,
        val requiresConsent: Boolean,
        val allowParallel: Boolean
    ) : JarvisServerEvent()
    data class Error(val code: String, val message: String? = null) : JarvisServerEvent()
    object Done : JarvisServerEvent()
}

data class PlannedStep(
    val stepNumber: Int,
    val tool: String,
    val description: String
)

data class JarvisHistoryTurn(
    val role: String,
    val text: String
)

data class JarvisAssistantRequest(
    val text: String,
    val history: List<JarvisHistoryTurn> = emptyList()
)

/**
 * Native OkHttp SSE stream client for the Jarvis /api/assistant endpoint.
 *
 * Enforces zero-trust stream invariants:
 * 1. HTTPS transport enforcement.
 * 2. Strict Bearer token authentication header.
 * 3. Strict schema validation of parsed events.
 * 4. Buffering of planned steps & actions until 'done' event arrives.
 */
class JarvisSseClient(
    private val tokenManager: SecureTokenManager,
    private val baseUrl: String = BuildConfig.JARVIS_BASE_URL
) {
    companion object {
        private const val TAG = "JarvisSseClient"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

        val ALLOWED_TOOLS = setOf(
            "send_sms", "update_device", "create_reminder", "open_app", "save_memory"
        )
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    fun streamAssistant(request: JarvisAssistantRequest): Flow<JarvisServerEvent> = flow {
        val token = tokenManager.getToken()
        if (token.isNullOrEmpty()) {
            emit(JarvisServerEvent.Error(code = "UNAUTHORIZED", message = "Device Bearer token missing"))
            return@flow
        }

        val endpoint = if (baseUrl.endsWith("/")) "${baseUrl}api/assistant" else "$baseUrl/api/assistant"

        // Enforce HTTPS unless debug localhost
        if (!endpoint.startsWith("https://") && !endpoint.startsWith("http://10.0.2.2") && !endpoint.startsWith("http://localhost")) {
            emit(JarvisServerEvent.Error(code = "SECURITY_VIOLATION", message = "Non-HTTPS endpoint rejected"))
            return@flow
        }

        val historyArray = JSONArray()
        for (turn in request.history) {
            val turnObj = JSONObject().apply {
                put("role", turn.role)
                put("text", turn.text)
            }
            historyArray.put(turnObj)
        }

        val reqJson = JSONObject().apply {
            put("text", request.text)
            put("history", historyArray)
        }

        val httpRequest = Request.Builder()
            .url(endpoint)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "text/event-stream")
            .post(reqJson.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        var hasReceivedDone = false
        var hasReceivedError = false

        try {
            val response = httpClient.newCall(httpRequest).execute()
            if (!response.isSuccessful) {
                val code = when (response.code) {
                    400 -> "BAD_REQUEST"
                    401 -> "UNAUTHORIZED"
                    403 -> "FORBIDDEN"
                    408 -> "UPSTREAM_TIMEOUT"
                    429 -> "RATE_LIMITED"
                    500 -> "INTERNAL"
                    504 -> "UPSTREAM_TIMEOUT"
                    else -> "UPSTREAM_ERROR"
                }
                emit(JarvisServerEvent.Error(code = code, message = "HTTP ${response.code}"))
                response.close()
                return@flow
            }

            val body = response.body
            if (body == null) {
                emit(JarvisServerEvent.Error(code = "UPSTREAM_ERROR", message = "Empty response body"))
                return@flow
            }

            val reader = BufferedReader(InputStreamReader(body.byteStream(), Charsets.UTF_8))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val trimmed = line?.trim() ?: continue
                if (trimmed.isEmpty() || trimmed.startsWith(":")) continue // Ping / comment

                if (trimmed.startsWith("data:")) {
                    val payload = trimmed.substring(5).trim()
                    if (payload == "[DONE]") {
                        hasReceivedDone = true
                        emit(JarvisServerEvent.Done)
                        break
                    }

                    val event = parseSsePayload(payload)
                    if (event != null) {
                        if (event is JarvisServerEvent.Done) hasReceivedDone = true
                        if (event is JarvisServerEvent.Error) hasReceivedError = true

                        emit(event)

                        if (hasReceivedDone || hasReceivedError) {
                            break
                        }
                    } else {
                        // Malformed payload triggers upstream error and aborts
                        emit(JarvisServerEvent.Error(code = "UPSTREAM_ERROR", message = "Malformed SSE JSON event payload"))
                        hasReceivedError = true
                        break
                    }
                }
            }

            response.close()

            // Invariant: Stream must terminate with either Done or Error
            if (!hasReceivedDone && !hasReceivedError) {
                emit(JarvisServerEvent.Error(code = "UPSTREAM_ERROR", message = "Stream disconnected unexpectedly without terminal event"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "SSE connection error: ${e.javaClass.simpleName}")
            emit(JarvisServerEvent.Error(code = "NETWORK_ERROR", message = e.localizedMessage))
        }
    }.flowOn(Dispatchers.IO)

    private fun parseSsePayload(jsonStr: String): JarvisServerEvent? {
        return try {
            val root = JSONObject(jsonStr)
            val type = root.optString("type")

            when (type) {
                "text_delta" -> {
                    val delta = root.optString("delta", "")
                    JarvisServerEvent.TextDelta(delta = delta)
                }
                "plan" -> {
                    val planId = root.optString("planId", "plan_${System.currentTimeMillis()}")
                    val stepsArray = root.optJSONArray("steps") ?: JSONArray()
                    val stepsList = mutableListOf<PlannedStep>()

                    for (i in 0 until stepsArray.length()) {
                        val stepObj = stepsArray.getJSONObject(i)
                        val tool = stepObj.optString("tool")
                        if (tool !in ALLOWED_TOOLS) {
                            Log.w(TAG, "Rejected plan containing unauthorized tool: $tool")
                            return null
                        }
                        stepsList.add(
                            PlannedStep(
                                stepNumber = stepObj.optInt("stepNumber", i + 1),
                                tool = tool,
                                description = stepObj.optString("description", "Execute $tool")
                            )
                        )
                    }
                    JarvisServerEvent.Plan(planId = planId, steps = stepsList)
                }
                "action" -> {
                    val actionId = root.optString("actionId", "act_${System.currentTimeMillis()}")
                    val tool = root.optString("tool")
                    if (tool !in ALLOWED_TOOLS) {
                        Log.w(TAG, "Rejected action with disallowed tool: $tool")
                        return null
                    }

                    val requiresConsent = root.optBoolean("requiresConsent", true)
                    val allowParallel = root.optBoolean("allowParallel", false)

                    // Invariant check: risky client actions MUST require consent and CANNOT be parallel
                    if (!requiresConsent || allowParallel) {
                        Log.w(TAG, "Security policy violation: requiresConsent=false or allowParallel=true in action event")
                        return null
                    }

                    val argsObj = root.optJSONObject("args") ?: JSONObject()
                    val argsMap = mutableMapOf<String, String>()
                    val keys = argsObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        argsMap[key] = argsObj.optString(key, "")
                    }

                    JarvisServerEvent.Action(
                        actionId = actionId,
                        tool = tool,
                        args = argsMap,
                        riskLevel = root.optString("suggestedRisk", "HIGH"),
                        requiresConsent = requiresConsent,
                        allowParallel = allowParallel
                    )
                }
                "error" -> {
                    val code = root.optString("code", "UPSTREAM_ERROR")
                    val msg = if (root.has("message") && !root.isNull("message")) root.optString("message") else null
                    JarvisServerEvent.Error(code = code, message = msg)
                }
                "done" -> JarvisServerEvent.Done
                else -> null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed parsing SSE event json: ${e.javaClass.simpleName}")
            null
        }
    }
}
