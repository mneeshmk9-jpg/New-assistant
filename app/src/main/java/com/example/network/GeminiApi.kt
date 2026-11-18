package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val role: String? = null,
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class ResponseFormatText(
    val mimeType: String
)

@JsonClass(generateAdapter = true)
data class ResponseFormat(
    val text: ResponseFormatText? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null,
    val generationConfig: GenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

// The assistant structured response format we demand from Gemini
@JsonClass(generateAdapter = true)
data class AssistantAction(
    val action: String, // "update_device", "create_reminder", "open_app", "web_search", "save_memory"
    val action_params: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class AssistantResponse(
    val reply: String,
    val speak: String,
    val action: String = "none", // Legacy fallback for single action
    val action_params: Map<String, String>? = null, // Legacy fallback
    val actions: List<AssistantAction>? = null, // Multi-step planning actions
    val thought: String? = null
)

@JsonClass(generateAdapter = true)
data class StreamPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class StreamContent(
    val parts: List<StreamPart>? = null
)

@JsonClass(generateAdapter = true)
data class StreamCandidate(
    val content: StreamContent? = null
)

@JsonClass(generateAdapter = true)
data class StreamChunk(
    val candidates: List<StreamCandidate>? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse

    @Streaming
    @POST("v1beta/models/gemini-3.5-flash:streamGenerateContent")
    suspend fun streamGenerateContent(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GenerateContentRequest,
        @Query("alt") alt: String = "sse"
    ): ResponseBody
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .apply {
            if (com.example.BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                    redactHeader("x-goog-api-key")
                    redactHeader("Authorization")
                })
            }
        }
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}
