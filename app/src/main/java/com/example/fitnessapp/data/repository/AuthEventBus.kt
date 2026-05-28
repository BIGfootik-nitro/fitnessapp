package com.example.fitnessapp.data.repository

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthEventBus {
    private val _events = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    fun emitUnauthorized() {
        _events.tryEmit(AuthEvent.Unauthorized)
    }
}

sealed class AuthEvent {
    data object Unauthorized : AuthEvent()
}
