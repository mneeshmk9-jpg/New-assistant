package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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

        // Explicit step-by-step migrations protecting forensic logs and mission records across version updates
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `audit_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `toolName` TEXT NOT NULL, `parameters` TEXT NOT NULL, `riskLevel` TEXT NOT NULL, `status` TEXT NOT NULL, `userConfirmation` INTEGER NOT NULL, `executionTimeMs` INTEGER NOT NULL, `errorMessage` TEXT, `rollbackSupported` INTEGER NOT NULL, `sessionId` TEXT NOT NULL, `permissionStatus` TEXT, `userIntent` TEXT, `llmPlan` TEXT, `confidenceScore` REAL NOT NULL, `policyDecision` TEXT, `capabilityCheck` TEXT, `userConsent` TEXT, `resultSummary` TEXT, `rollbackDetails` TEXT, `forensicChainJson` TEXT)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `policy_rules` (`id` TEXT PRIMARY KEY NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `isEnabled` INTEGER NOT NULL, `configValue` TEXT, `priority` TEXT NOT NULL)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `golden_missions` (`id` TEXT PRIMARY KEY NOT NULL, `category` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `sampleQueries` TEXT NOT NULL, `expectedActionsJson` TEXT NOT NULL, `requiredAnchorsJson` TEXT NOT NULL, `minPlanningScore` REAL NOT NULL, `minExecutionScore` REAL NOT NULL)")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `mission_certifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `missionId` TEXT NOT NULL, `userQuery` TEXT NOT NULL, `status` TEXT NOT NULL, `planningScore` REAL NOT NULL, `executionScore` REAL NOT NULL, `totalScore` REAL NOT NULL, `verifiedAnchors` TEXT NOT NULL, `missingAnchors` TEXT NOT NULL, `certificationNotes` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Ensure forensic columns and certification tables exist without data loss
                db.execSQL("CREATE TABLE IF NOT EXISTS `mission_certifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `missionId` TEXT NOT NULL, `userQuery` TEXT NOT NULL, `status` TEXT NOT NULL, `planningScore` REAL NOT NULL, `executionScore` REAL NOT NULL, `totalScore` REAL NOT NULL, `verifiedAnchors` TEXT NOT NULL, `missingAnchors` TEXT NOT NULL, `certificationNotes` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
            }
        }

        val MIGRATION_1_6 = object : Migration(1, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `audit_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `toolName` TEXT NOT NULL, `parameters` TEXT NOT NULL, `riskLevel` TEXT NOT NULL, `status` TEXT NOT NULL, `userConfirmation` INTEGER NOT NULL, `executionTimeMs` INTEGER NOT NULL, `errorMessage` TEXT, `rollbackSupported` INTEGER NOT NULL, `sessionId` TEXT NOT NULL, `permissionStatus` TEXT, `userIntent` TEXT, `llmPlan` TEXT, `confidenceScore` REAL NOT NULL, `policyDecision` TEXT, `capabilityCheck` TEXT, `userConsent` TEXT, `resultSummary` TEXT, `rollbackDetails` TEXT, `forensicChainJson` TEXT)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `policy_rules` (`id` TEXT PRIMARY KEY NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `isEnabled` INTEGER NOT NULL, `configValue` TEXT, `priority` TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `golden_missions` (`id` TEXT PRIMARY KEY NOT NULL, `category` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `sampleQueries` TEXT NOT NULL, `expectedActionsJson` TEXT NOT NULL, `requiredAnchorsJson` TEXT NOT NULL, `minPlanningScore` REAL NOT NULL, `minExecutionScore` REAL NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `mission_certifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `missionId` TEXT NOT NULL, `userQuery` TEXT NOT NULL, `status` TEXT NOT NULL, `planningScore` REAL NOT NULL, `executionScore` REAL NOT NULL, `totalScore` REAL NOT NULL, `verifiedAnchors` TEXT NOT NULL, `missingAnchors` TEXT NOT NULL, `certificationNotes` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
            }
        }

        val MIGRATION_3_6 = object : Migration(3, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `golden_missions` (`id` TEXT PRIMARY KEY NOT NULL, `category` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `sampleQueries` TEXT NOT NULL, `expectedActionsJson` TEXT NOT NULL, `requiredAnchorsJson` TEXT NOT NULL, `minPlanningScore` REAL NOT NULL, `minExecutionScore` REAL NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `mission_certifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `missionId` TEXT NOT NULL, `userQuery` TEXT NOT NULL, `status` TEXT NOT NULL, `planningScore` REAL NOT NULL, `executionScore` REAL NOT NULL, `totalScore` REAL NOT NULL, `verifiedAnchors` TEXT NOT NULL, `missingAnchors` TEXT NOT NULL, `certificationNotes` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rohit_assistant_database"
                )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_1_6,
                    MIGRATION_3_6
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
