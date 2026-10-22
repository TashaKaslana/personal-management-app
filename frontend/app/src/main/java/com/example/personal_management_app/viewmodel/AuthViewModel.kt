package com.example.personal_management_app.viewmodel;

import androidx.lifecycle.ViewModel
import com.example.personal_management_app.core.service.SessionManager
import com.example.personal_management_app.core.service.UserSession
import com.example.personal_management_app.dtos.AuthResult
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

        return when (val result = userRepository.login(email, password)) {
            is AuthResult.Success -> {
                updateSession(result)
                null
            }

            AuthResult.WrongCredentials -> "Email hoặc mật khẩu không đúng!"
            AuthResult.EmailTaken -> "Email đã được sử dụng!"
        }
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

        return when (val result = userRepository.register(fullName, email, password)) {
            is AuthResult.Success -> {
                updateSession(result)
                null
            }

            AuthResult.EmailTaken -> "Email đã được sử dụng!"
            AuthResult.WrongCredentials -> "Email hoặc mật khẩu không đúng!"
        }
    }

    fun forgotPassword(email: String): String? {
        if (email.isBlank()) {
            return "Vui lòng nhập địa chỉ email!"
        }
        return null
    }

    private fun updateSession(result: AuthResult.Success) {
        sessionManager.updateSession(
            UserSession(
                userId = result.user.id,
                token = result.token,
                username = result.user.username
            )
        )
    }
}
