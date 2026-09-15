package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<ConversationMessage>>

    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ConversationMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ConversationMessage): Long

    @Update
    suspend fun updateMessage(message: ConversationMessage)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: Int)

    @Query("DELETE FROM messages WHERE sessionId = :sessionId")
    suspend fun clearSessionMessages(sessionId: String)

    @Query("DELETE FROM messages")
    suspend fun clearHistory()

    @Query("UPDATE messages SET text = REPLACE(text, 'Vani', 'Rohit')")
    suspend fun renameVaniToRohit()

    @Query("UPDATE messages SET text = REPLACE(text, 'vani', 'rohit')")
    suspend fun renameVaniToRohitLower()
}

@Dao
interface ChatSessionDao {
    @Query("SELECT * FROM chat_sessions ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllSessions(): Flow<List<ChatSession>>

    @Query("SELECT * FROM chat_sessions WHERE id = :id")
    suspend fun getSessionById(id: String): ChatSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSession(session: ChatSession)

    @Query("UPDATE chat_sessions SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePinned(id: String, isPinned: Boolean)

    @Query("UPDATE chat_sessions SET title = :title, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTitle(id: String, title: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM chat_sessions WHERE id = :id")
    suspend fun deleteSession(id: String)

    @Query("DELETE FROM chat_sessions")
    suspend fun clearAllSessions()
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<MemoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryItem)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: Int)

    @Query("DELETE FROM memories")
    suspend fun clearAllMemories()

    @Query("UPDATE memories SET info = REPLACE(info, 'Vani', 'Rohit')")
    suspend fun renameVaniToRohit()

    @Query("UPDATE memories SET info = REPLACE(info, 'vani', 'rohit')")
    suspend fun renameVaniToRohitLower()
}

@Dao
interface DeviceDao {
    @Query("SELECT * FROM smart_devices")
    fun getAllDevices(): Flow<List<SmartDevice>>

    @Query("SELECT * FROM smart_devices WHERE id = :id")
    suspend fun getDeviceById(id: String): SmartDevice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDevice(device: SmartDevice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<SmartDevice>)

    @Query("SELECT COUNT(*) FROM smart_devices")
    suspend fun getCount(): Int
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY timestamp DESC")
    fun getAllReminders(): Flow<List<ReminderItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderItem)

    @Update
    suspend fun updateReminder(reminder: ReminderItem)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminder(id: Int)

    @Query("DELETE FROM reminders")
    suspend fun clearAllReminders()
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog): Long

    @Query("DELETE FROM audit_logs")
    suspend fun clearAllLogs()
}

@Dao
interface PolicyRuleDao {
    @Query("SELECT * FROM policy_rules")
    fun getAllPolicies(): Flow<List<PolicyRule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePolicy(policy: PolicyRule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolicies(policies: List<PolicyRule>)

    @Query("SELECT COUNT(*) FROM policy_rules")
    suspend fun getCount(): Int
}

@Dao
interface GoldenMissionDao {
    @Query("SELECT * FROM golden_missions")
    fun getAllMissions(): Flow<List<GoldenMission>>

    @Query("SELECT * FROM golden_missions WHERE id = :id")
    suspend fun getMissionById(id: String): GoldenMission?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<GoldenMission>)

    @Query("SELECT COUNT(*) FROM golden_missions")
    suspend fun getCount(): Int
}

@Dao
interface MissionCertificationDao {
    @Query("SELECT * FROM mission_certifications ORDER BY timestamp DESC")
    fun getAllCertifications(): Flow<List<MissionCertificationRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertification(cert: MissionCertificationRecord): Long

    @Query("DELETE FROM mission_certifications")
    suspend fun clearAllCertifications()
}


