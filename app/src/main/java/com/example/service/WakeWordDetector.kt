package com.example.service

/**
 * Common contract for on-device wake-word detection engines
 * (e.g., Porcupine, openWakeWord, or energy-efficient acoustic matcher).
 */
interface WakeWordDetector {
    val engineName: String
    val isRunning: Boolean
    fun start(onDetected: (keyword: String) -> Unit, onError: (error: String) -> Unit)
    fun stop()
}
