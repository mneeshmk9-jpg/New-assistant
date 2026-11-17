package com.example.jarvis

import com.example.certification.ActionState
import com.example.database.SmartDevice
import com.example.viewmodel.ProposedAction
import com.example.viewmodel.RiskLevel

sealed class LocalIntentResult {
    data class Success(
        val actions: List<ProposedAction>,
        val responseText: String
    ) : LocalIntentResult()

    object NotHandled : LocalIntentResult()
}

/**
 * Deterministic on-device intent parser for offline resilience.
 *
 * Security invariants:
 * 1. Emits only ProposedAction instances strictly within the 5-tool allow-list
 *    (send_sms, update_device, create_reminder, open_app, save_memory).
 * 2. Never executes actions directly — callers must route actions through
 *    AssistantViewModel.validatePlanActions + setPendingActions (Pre-Consent Gate).
 */
class LocalIntentRouter {

    fun parseAndRoute(
        input: String,
        knownDevices: List<SmartDevice> = emptyList()
    ): LocalIntentResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return LocalIntentResult.NotHandled
        val lower = trimmed.lowercase()

        // 1. Flashlight / Torch control -> update_device("flashlight")
        if (lower.contains("flashlight on") || lower.contains("torch on") ||
            lower.contains("turn on flashlight") || lower.contains("turn on torch") ||
            lower.contains("flashlight chalu") || lower.contains("torch chalu") ||
            lower.contains("टॉर्च जला") || lower.contains("टॉर्च चालू") ||
            lower.contains("फ्लैशलाइट चालू")
        ) {
            return LocalIntentResult.Success(
                actions = listOf(
                    ProposedAction(
                        action = "update_device",
                        description = "Turn ON device flashlight",
                        originalParams = mapOf("device_id" to "flashlight", "state" to "on"),
                        editedParams = mapOf("device_id" to "flashlight", "state" to "on"),
                        riskLevel = RiskLevel.HIGH,
                        supportsRollback = true,
                        state = ActionState.PROPOSED
                    )
                ),
                responseText = "फ़्लैशलाइट चालू करने के लिए नीचे कंसेंट की पुष्टि करें।"
            )
        }

        if (lower.contains("flashlight off") || lower.contains("torch off") ||
            lower.contains("turn off flashlight") || lower.contains("turn off torch") ||
            lower.contains("flashlight band") || lower.contains("torch band") ||
            lower.contains("टॉर्च बंद") || lower.contains("फ्लैशलाइट बंद")
        ) {
            return LocalIntentResult.Success(
                actions = listOf(
                    ProposedAction(
                        action = "update_device",
                        description = "Turn OFF device flashlight",
                        originalParams = mapOf("device_id" to "flashlight", "state" to "off"),
                        editedParams = mapOf("device_id" to "flashlight", "state" to "off"),
                        riskLevel = RiskLevel.HIGH,
                        supportsRollback = true,
                        state = ActionState.PROPOSED
                    )
                ),
                responseText = "फ़्लैशलाइट बंद करने के लिए नीचे कंसेंट की पुष्टि करें।"
            )
        }

        // 2. Open App Intent -> open_app
        val openPrefixes = listOf("open ", "launch ", "start ")
        for (prefix in openPrefixes) {
            if (lower.startsWith(prefix)) {
                val appName = trimmed.substring(prefix.length).trim().take(60)
                if (appName.isNotEmpty()) {
                    return LocalIntentResult.Success(
                        actions = listOf(
                            ProposedAction(
                                action = "open_app",
                                description = "Open app: $appName",
                                originalParams = mapOf("app_package" to appName),
                                editedParams = mapOf("app_package" to appName),
                                riskLevel = RiskLevel.LOW,
                                supportsRollback = false,
                                state = ActionState.PROPOSED
                            )
                        ),
                        responseText = "$appName खोलने के लिए तैयार हूँ। कृपया कंसेंट की पुष्टि करें।"
                    )
                }
            }
        }
        if (lower.endsWith(" kholo") || lower.endsWith(" खोलो")) {
            val appName = trimmed
                .replace(Regex("(?i)\\s+(kholo|खोलो)$"), "")
                .trim()
                .take(60)
            if (appName.isNotEmpty()) {
                return LocalIntentResult.Success(
                    actions = listOf(
                        ProposedAction(
                            action = "open_app",
                            description = "Open app: $appName",
                            originalParams = mapOf("app_package" to appName),
                            editedParams = mapOf("app_package" to appName),
                            riskLevel = RiskLevel.LOW,
                            supportsRollback = false,
                            state = ActionState.PROPOSED
                        )
                    ),
                    responseText = "$appName खोलने के लिए तैयार हूँ। कृपया कंसेंट की पुष्टि करें।"
                )
            }
        }

        // 3. Offline Reminder -> create_reminder
        val reminderPrefixes = listOf("remind me to ", "set reminder ", "add reminder ", "reminder ")
        for (prefix in reminderPrefixes) {
            if (lower.startsWith(prefix)) {
                val title = trimmed.substring(prefix.length).trim().take(200)
                if (title.isNotEmpty()) {
                    return LocalIntentResult.Success(
                        actions = listOf(
                            ProposedAction(
                                action = "create_reminder",
                                description = "Create reminder: $title",
                                originalParams = mapOf("title" to title, "category" to "Offline"),
                                editedParams = mapOf("title" to title, "category" to "Offline"),
                                riskLevel = RiskLevel.MEDIUM,
                                supportsRollback = true,
                                state = ActionState.PROPOSED
                            )
                        ),
                        responseText = "ऑफ़लाइन रिमाइंडर सेट करने के लिए तैयार हूँ: \"$title\"। कृपया पुष्टि करें।"
                    )
                }
            }
        }

        // 4. Offline Memory -> save_memory
        val memoryPrefixes = listOf("remember that ", "save memory ", "remember ")
        for (prefix in memoryPrefixes) {
            if (lower.startsWith(prefix)) {
                val fact = trimmed.substring(prefix.length).trim().take(300)
                if (fact.isNotEmpty()) {
                    return LocalIntentResult.Success(
                        actions = listOf(
                            ProposedAction(
                                action = "save_memory",
                                description = "Save memory: $fact",
                                originalParams = mapOf("fact" to fact),
                                editedParams = mapOf("fact" to fact),
                                riskLevel = RiskLevel.MEDIUM,
                                supportsRollback = true,
                                state = ActionState.PROPOSED
                            )
                        ),
                        responseText = "इसे लोकल मेमोरी में सेव करने के लिए पुष्टि करें।"
                    )
                }
            }
        }

        // 5. Known Local Smart Home Devices -> update_device
        if (knownDevices.isNotEmpty()) {
            val targetState = when {
                lower.contains("turn on") || lower.contains("switch on") || lower.endsWith(" on") || lower.contains("chalu") -> "on"
                lower.contains("turn off") || lower.contains("switch off") || lower.endsWith(" off") || lower.contains("band") -> "off"
                lower.contains("unlock") -> "unlocked"
                lower.contains("lock") -> "locked"
                else -> null
            }

            if (targetState != null) {
                val matchedDevice = knownDevices.firstOrNull { device ->
                    lower.contains(device.name.lowercase()) || lower.contains(device.id.lowercase())
                }
                if (matchedDevice != null) {
                    return LocalIntentResult.Success(
                        actions = listOf(
                            ProposedAction(
                                action = "update_device",
                                description = "Update ${matchedDevice.name} to $targetState",
                                originalParams = mapOf("device_id" to matchedDevice.id, "state" to targetState),
                                editedParams = mapOf("device_id" to matchedDevice.id, "state" to targetState),
                                riskLevel = RiskLevel.HIGH,
                                supportsRollback = true,
                                state = ActionState.PROPOSED
                            )
                        ),
                        responseText = "${matchedDevice.name} को '$targetState' करने के लिए नीचे कंसेंट की पुष्टि करें।"
                    )
                }
            }
        }

        return LocalIntentResult.NotHandled
    }
}
