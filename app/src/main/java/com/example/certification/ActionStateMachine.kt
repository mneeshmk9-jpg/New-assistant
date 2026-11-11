package com.example.certification

import java.security.MessageDigest

/**
 * ActionState defines the formal state machine transitions for an autonomous or semi-autonomous action.
 * PROPOSED -> VALIDATING -> AWAITING_CONSENT (if risk requires) -> AUTHORIZED -> EXECUTING -> SUCCEEDED / FAILED -> ROLLED_BACK
 */
enum class ActionState {
    PROPOSED,
    VALIDATING,
    BLOCKED,
    AWAITING_CONSENT,
    AUTHORIZED,
    EXECUTING,
    CANCEL_REQUESTED,
    SUCCEEDED,
    FAILED,
    ROLLBACK_PENDING,
    ROLLING_BACK,
    ROLLED_BACK,
    ROLLBACK_FAILED,
    RECOVERY_REQUIRED
}

/**
 * ConsentToken cryptographically binds the exact user approval to the canonical action payload,
 * timestamp, and actionId to prevent replay attacks or parameter substitution tampering.
 */
data class ConsentToken(
    val actionId: String,
    val canonicalPayloadHash: String,
    val authorizedTimestamp: Long = System.currentTimeMillis(),
    val signature: String
) {
    fun verify(actionId: String, actionName: String, params: Map<String, String>): Boolean {
        return verify(this, actionId, actionName, params)
    }

    companion object {
        fun generate(actionId: String, actionName: String, params: Map<String, String>, timestamp: Long = System.currentTimeMillis()): ConsentToken {
            val canonicalPayload = buildCanonicalPayload(actionName, params)
            val payloadHash = sha256(canonicalPayload)
            val signature = sha256("$actionId:$payloadHash:$timestamp:VISHAL_AI_CONSENT_SECRET")
            return ConsentToken(
                actionId = actionId,
                canonicalPayloadHash = payloadHash,
                authorizedTimestamp = timestamp,
                signature = signature
            )
        }

        fun verify(token: ConsentToken, actionId: String, actionName: String, params: Map<String, String>): Boolean {
            if (token.actionId != actionId) return false
            val canonicalPayload = buildCanonicalPayload(actionName, params)
            val currentHash = sha256(canonicalPayload)
            if (token.canonicalPayloadHash != currentHash) return false
            val expectedSignature = sha256("$actionId:$currentHash:${token.authorizedTimestamp}:VISHAL_AI_CONSENT_SECRET")
            return token.signature == expectedSignature
        }

        fun buildCanonicalPayload(actionName: String, params: Map<String, String>): String {
            val sortedParams = params.entries.sortedBy { it.key }.joinToString("&") { "${it.key}=${it.value}" }
            return "action=$actionName&$sortedParams"
        }

        fun sha256(input: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
            return hashBytes.joinToString("") { "%02x".format(it) }
        }
    }
}
