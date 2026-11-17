package com.example.jarvis

import com.example.viewmodel.ProposedAction
import com.example.viewmodel.RiskLevel

/**
 * Encapsulates the zero-trust streaming state machine for Jarvis assistant events.
 *
 * Invariant guarantees:
 * 1. Proposed actions and planned steps are strictly buffered in memory during streaming.
 * 2. Under NO circumstances does [pendingActions] become non-null before [onDone] is called.
 * 3. Any error event or exception immediately wipes all buffers and leaves [pendingActions] null.
 * 4. Pre-consent gate validation runs on buffered actions upon [onDone].
 */
class JarvisStreamEventBuffer(
    private val allowedTools: Set<String> = JarvisSseClient.ALLOWED_TOOLS
) {
    private val _bufferedActions = mutableListOf<ProposedAction>()
    val bufferedActions: List<ProposedAction> get() = _bufferedActions

    private val _bufferedPlanSteps = mutableListOf<PlannedStep>()
    val bufferedPlanSteps: List<PlannedStep> get() = _bufferedPlanSteps

    var pendingActions: List<ProposedAction>? = null
        private set

    var isErrorState: Boolean = false
        private set

    var isCompleted: Boolean = false
        private set

    var blockedToolDueToPolicy: String? = null
        private set

    fun onTextDelta(delta: String) {
        // Text streaming does not affect action buffer
    }

    fun onPlan(plan: JarvisServerEvent.Plan) {
        if (isErrorState) return
        _bufferedPlanSteps.clear()
        _bufferedPlanSteps.addAll(plan.steps)
    }

    fun onAction(actionEvent: JarvisServerEvent.Action) {
        if (isErrorState) return
        val desc = when (actionEvent.tool) {
            "send_sms" -> "Send SMS to ${actionEvent.args["recipient"] ?: "someone"}"
            "update_device", "control_device" -> "Update ${actionEvent.args["device_id"] ?: actionEvent.args["device"] ?: "device"} to ${actionEvent.args["state"] ?: "state"}"
            "create_reminder" -> "Create reminder: ${actionEvent.args["title"] ?: "task"}"
            "open_app" -> "Open app: ${actionEvent.args["app_package"] ?: "app"}"
            "save_memory" -> "Save memory: ${actionEvent.args["fact"] ?: "fact"}"
            else -> "Execute ${actionEvent.tool}"
        }
        val risk = try {
            RiskLevel.valueOf(actionEvent.riskLevel.uppercase())
        } catch (e: Exception) {
            RiskLevel.HIGH
        }

        // Invariant: Action is only added to internal buffer. pendingActions remains strictly null.
        _bufferedActions.add(
            ProposedAction(
                id = actionEvent.actionId,
                action = actionEvent.tool,
                description = desc,
                originalParams = actionEvent.args,
                editedParams = actionEvent.args,
                riskLevel = risk,
                supportsRollback = actionEvent.tool in listOf("update_device", "control_device", "save_memory")
            )
        )
    }

    fun onError(error: JarvisServerEvent.Error) {
        isErrorState = true
        _bufferedActions.clear()
        _bufferedPlanSteps.clear()
        pendingActions = null
    }

    fun onDone(): Boolean {
        if (isErrorState) {
            pendingActions = null
            return false
        }
        isCompleted = true

        if (_bufferedActions.isEmpty()) {
            pendingActions = null
            return true
        }

        // Pre-consent gate validation
        val disallowed = _bufferedActions.find { it.action !in allowedTools }
        if (disallowed != null) {
            blockedToolDueToPolicy = disallowed.action
            _bufferedActions.clear()
            pendingActions = null
            return false
        }

        // Only after Done and policy validation are actions promoted
        pendingActions = _bufferedActions.toList()
        return true
    }
}
