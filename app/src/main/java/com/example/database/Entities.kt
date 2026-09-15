package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val model: String = "GPT-4o",
    val personaId: String = "general"
)

@Entity(tableName = "messages")
data class ConversationMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String, // "user" or "assistant"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isToolCall: Boolean = false,
    val toolName: String? = null,
    val sessionId: String = "default",
    val thought: String? = null,
    val modelUsed: String = "GPT-4o",
    val rating: Int = 0, // 0 = none, 1 = up, -1 = down
    val searchSources: String? = null
)

@Entity(tableName = "memories")
data class MemoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val info: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "smart_devices")
data class SmartDevice(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // "light", "thermostat", "lock", "switch"
    val state: String, // "on", "off", "24°C", "locked"
    val room: String
)

@Entity(tableName = "reminders")
data class ReminderItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String = "General", // "General", "Shopping", "Work"
    val completed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val toolName: String,
    val parameters: String,
    val riskLevel: String,
    val status: String, // "SUCCESS", "FAILED", "ROLLED_BACK", "BLOCKED"
    val userConfirmation: Boolean,
    val executionTimeMs: Long,
    val errorMessage: String? = null,
    val rollbackSupported: Boolean = false,
    val sessionId: String = "SESS-001",
    val permissionStatus: String? = "GRANTED",
    // Forensic Decision Lifecycle Chain
    val userIntent: String? = null,
    val llmPlan: String? = null,
    val confidenceScore: Double = 0.95,
    val policyDecision: String? = "ALLOWED", // "ALLOWED", "BLOCKED_BY_CURFEW", "SYSTEM_POLICY_WINS", "REQUIRES_CONSENT"
    val capabilityCheck: String? = "CAPABLE",
    val userConsent: String? = "CONFIRMED", // "CONFIRMED", "REJECTED", "NOT_REQUIRED"
    val resultSummary: String? = null,
    val rollbackDetails: String? = null,
    val forensicChainJson: String? = null
)

@Entity(tableName = "policy_rules")
data class PolicyRule(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val isEnabled: Boolean,
    val configValue: String? = null,
    val priority: String = "USER" // "SYSTEM", "SECURITY", "USER", "SESSION"
)

@Entity(tableName = "golden_missions")
data class GoldenMission(
    @PrimaryKey val id: String,
    val category: String, // "Smart Home", "Reminder", "Messaging", "Memory", "App Launch"
    val title: String,
    val description: String,
    val sampleQueries: String, // JSON array or comma separated
    val expectedActionsJson: String, // JSON list of tool/action specs
    val requiredAnchorsJson: String, // JSON list of critical evidence anchors
    val minPlanningScore: Double = 80.0,
    val minExecutionScore: Double = 80.0
)

@Entity(tableName = "mission_certifications")
data class MissionCertificationRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val missionId: String,
    val userQuery: String,
    val status: String, // "CERTIFIED", "UNCERTIFIED", "FAILED"
    val planningScore: Double, // 0..100
    val executionScore: Double, // 0..100
    val totalScore: Double,
    val verifiedAnchors: String,
    val missingAnchors: String,
    val certificationNotes: String,
    val timestamp: Long = System.currentTimeMillis()
)


