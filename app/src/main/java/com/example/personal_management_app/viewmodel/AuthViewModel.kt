package com.example.personal_management_app.viewmodel;

import androidx.lifecycle.ViewModel
import com.example.personal_management_app.core.service.SessionManager
import com.example.personal_management_app.core.service.UserSession
import com.example.personal_management_app.repositories.UserRepository
import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) : ViewModel() {
    fun getSession(): StateFlow<UserSession?> {
        return sessionManager.sessionState
    }

    fun login(email: String, password: String): String? {
        if (email.isBlank() || password.isBlank()) {
            return "Vui lòng nhập đầy đủ email và mật khẩu!"
        }

        val session = userRepository.login(email, password) ?: return "Email hoặc mật khẩu không đúng!"
        sessionManager.updateSession(session)

        return null
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        if (fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return "Vui lòng nhập đầy đủ tất cả các trường!"
        }

        if (password != confirmPassword) {
            return "Mật khẩu xác nhận không khớp!"
        }

        if (password.length < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự!"
        }

        val session = userRepository.register(fullName, email, password) ?: return "Email đã được sử dụng!"
        sessionManager.updateSession(session)

        return null
    }
}
