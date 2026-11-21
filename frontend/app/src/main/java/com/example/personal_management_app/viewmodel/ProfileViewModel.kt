package com.example.personal_management_app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personal_management_app.core.service.SessionManager
import com.example.personal_management_app.entities.ProfileEntity
import com.example.personal_management_app.repositories.UserRepository
import com.example.personal_management_app.utils.copyUriToAvatarStorage
import com.example.personal_management_app.utils.deleteAvatarFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentProfile: StateFlow<ProfileEntity?> = sessionManager.sessionState
        .flatMapLatest { session ->
            val userId = session?.userId ?: "user_01"
            userRepository.getProfileFlow(userId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = userRepository.getProfile(sessionManager.sessionState.value?.userId ?: "user_01")
        )

    fun getProfile(): ProfileEntity? {
        val userId = sessionManager.sessionState.value?.userId ?: "user_01"
        return userRepository.getProfile(userId)
    }

    fun uploadAvatar(context: Context, uri: Uri): Boolean {
        val userId = sessionManager.sessionState.value?.userId ?: "user_01"
        val savedPath = copyUriToAvatarStorage(context, uri, userId) ?: return false
        return userRepository.updateAvatar(userId, savedPath)
    }

    fun removeAvatar(context: Context): Boolean {
        val userId = sessionManager.sessionState.value?.userId ?: "user_01"
        deleteAvatarFile(context, userId)
        return userRepository.updateAvatar(userId, null)
    }

    fun updateProfile(
        name: String,
        username: String,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
        avatar: String? = null
    ): String? {
        if (name.isBlank() || username.isBlank()) {
            return "Vui lòng nhập đầy đủ họ tên và tên đăng nhập!"
        }
        val userId = sessionManager.sessionState.value?.userId ?: "user_01"

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
        if (!userRepository.updateProfile(userId, name, username, avatar, keepExistingAvatarIfNull = true)) {
            return "Không tìm thấy người dùng!"
        }
        return null
    }

    fun logout() {
        sessionManager.clearSession()
    }
}
