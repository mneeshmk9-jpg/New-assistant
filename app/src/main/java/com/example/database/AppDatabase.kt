package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ChatSession::class,
        ConversationMessage::class,
        MemoryItem::class,
        SmartDevice::class,
        ReminderItem::class,
        AuditLog::class,
        PolicyRule::class,
        GoldenMission::class,
        MissionCertificationRecord::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatSessionDao(): ChatSessionDao
    abstract fun messageDao(): MessageDao
    abstract fun memoryDao(): MemoryDao
    abstract fun deviceDao(): DeviceDao
    abstract fun reminderDao(): ReminderDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun policyRuleDao(): PolicyRuleDao
    abstract fun goldenMissionDao(): GoldenMissionDao
    abstract fun missionCertificationDao(): MissionCertificationDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rohit_assistant_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
