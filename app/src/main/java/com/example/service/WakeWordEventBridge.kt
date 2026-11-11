package com.example.service

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class WakeWordEvent {
    data class Detected(val keyword: String, val timestamp: Long = System.currentTimeMillis()) : WakeWordEvent()
    data class StatusChanged(val isListening: Boolean, val message: String = "") : WakeWordEvent()
    data class Error(val error: String) : WakeWordEvent()
}

/**
 * Decoupled event bridge between the foreground wake word service
 * and the presentation/UI layer (AssistantViewModel and Jetpack Compose).
 */
object WakeWordEventBridge {
    private val _events = MutableSharedFlow<WakeWordEvent>(extraBufferCapacity = 16)
    val events: SharedFlow<WakeWordEvent> = _events.asSharedFlow()

    fun emitEvent(event: WakeWordEvent) {
        _events.tryEmit(event)
    }
}
