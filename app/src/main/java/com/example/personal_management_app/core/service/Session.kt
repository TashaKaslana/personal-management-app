package com.example.personal_management_app.core.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Singleton

data class UserSession(
    val userId: String,
    val token: String,
    val username: String,
)

@Singleton
class SessionManager {
    private val _sessionState = MutableStateFlow<UserSession?>(null)
    val sessionState: StateFlow<UserSession?> = _sessionState.asStateFlow()

    fun updateSession(session: UserSession) {
        _sessionState.value = session
    }

    fun clearSession() {
        _sessionState.value = null
    }
}