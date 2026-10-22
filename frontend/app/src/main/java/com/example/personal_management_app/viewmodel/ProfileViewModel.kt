package com.example.personal_management_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.personal_management_app.core.service.SessionManager
import com.example.personal_management_app.entities.ProfileEntity
import com.example.personal_management_app.repositories.UserRepository
import javax.inject.Inject

import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) : ViewModel() {

    fun getProfile(): ProfileEntity? {
        val userId = sessionManager.sessionState.value?.userId ?: return null
        return userRepository.getProfile(userId)
    }

    fun updateProfile(
        name: String,
        username: String,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): String? {
        if (name.isBlank() || username.isBlank()) {
            return "Vui lòng nhập đầy đủ họ tên và tên đăng nhập!"
        }
        val userId = sessionManager.sessionState.value?.userId
            ?: return "Phiên đăng nhập đã hết hạn!"

        if (newPassword.isNotEmpty() || confirmPassword.isNotEmpty()) {
            if (currentPassword.isBlank()) {
                return "Vui lòng nhập mật khẩu hiện tại!"
            }
            if (newPassword != confirmPassword) {
                return "Mật khẩu mới không khớp nhau!"
            }
            if (newPassword.length < 6) {
                return "Mật khẩu phải có ít nhất 6 ký tự!"
            }
            if (!userRepository.changePassword(userId, currentPassword, newPassword)) {
                return "Mật khẩu hiện tại không đúng!"
            }
        }
        if (!userRepository.updateProfile(userId, name, username)) {
            return "Không tìm thấy người dùng!"
        }
        return null
    }

    fun logout() {
        sessionManager.clearSession()
    }
}
