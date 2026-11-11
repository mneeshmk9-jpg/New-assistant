package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.BuildConfig
import com.example.MainActivity

/**
 * Dedicated foreground service that runs the on-device wake-word detection engine
 * ("Hey Vani" / "हे वाणी").
 *
 * Keeps audio capture decoupled from UI/ViewModel and adheres to Android 14+
 * FOREGROUND_SERVICE_MICROPHONE rules.
 */
class WakeWordService : Service() {

    companion object {
        const val TAG = "WakeWordService"
        const val CHANNEL_ID = "vani_wake_word_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START_LISTENING = "com.example.service.ACTION_START_LISTENING"
        const val ACTION_STOP_LISTENING = "com.example.service.ACTION_STOP_LISTENING"

        fun startService(context: Context) {
            val intent = Intent(context, WakeWordService::class.java).apply {
                action = ACTION_START_LISTENING
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, WakeWordService::class.java).apply {
                action = ACTION_STOP_LISTENING
            }
            context.stopService(intent)
        }
    }

    private var isListening = false
    private var detector: WakeWordDetector? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_LISTENING -> {
                stopWakeWordEngine()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                startForegroundServiceWithNotification()
                startWakeWordEngine()
            }
        }
        return START_STICKY
    }

    private fun startForegroundServiceWithNotification() {
        val notification = createForegroundNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createForegroundNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Hey Vani Assistant")
            .setContentText("Listening on-device for 'Hey Vani' / 'हे वाणी'...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Hey Vani Wake Word Listener",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Foreground background audio listening for on-device wake-word detection"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun startWakeWordEngine() {
        if (isListening) return
        isListening = true
        Log.i(TAG, "Starting on-device native voice background service for Vani AI")

        WakeWordEventBridge.emitEvent(
            WakeWordEvent.StatusChanged(isListening = true, message = "Vani Assistant voice service active")
        )
    }

    private fun stopWakeWordEngine() {
        if (!isListening && detector == null) return
        isListening = false
        Log.i(TAG, "Stopping on-device wake word engine")
        try {
            detector?.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping detector", e)
        } finally {
            detector = null
        }
        WakeWordEventBridge.emitEvent(
            WakeWordEvent.StatusChanged(isListening = false, message = "Stopped")
        )
    }

    override fun onDestroy() {
        stopWakeWordEngine()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
