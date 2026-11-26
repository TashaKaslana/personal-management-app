package com.example.personal_management_app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personal_management_app.core.service.SessionManager
import com.example.personal_management_app.data.entities.ProfileEntity
import com.example.personal_management_app.data.repositories.UserRepository
import com.example.personal_management_app.utils.copyUriToAvatarStorage
import com.example.personal_management_app.utils.deleteAvatarFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentUserId: String?
        get() = sessionManager.sessionState.value?.userId

    @OptIn(ExperimentalCoroutinesApi::class)
    val profileFlow: StateFlow<ProfileEntity?> = sessionManager.sessionState
        .flatMapLatest { session ->
            val userId = session?.userId
            if (userId != null) {
                userRepository.getProfileFlow(userId)
            } else {
                flowOf(null)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = sessionManager.sessionState.value?.userId?.let { userRepository.getProfile(it) }
        )

    fun getProfile(): ProfileEntity? {
        val userId = currentUserId ?: return null
        return userRepository.getProfile(userId)
    }

    fun uploadAvatar(context: Context, uri: Uri): String? {
        val userId = currentUserId ?: return null
        val savedPath = copyUriToAvatarStorage(context, uri, userId) ?: return null
        userRepository.updateAvatar(userId, savedPath)
        return savedPath
    }

    fun removeAvatar(context: Context) {
        val userId = currentUserId ?: return
        deleteAvatarFile(context, userId)
        userRepository.updateAvatar(userId, null)
    }

    fun updateProfile(
        name: String,
        username: String,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): String? {
        val userId = currentUserId ?: return "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!"

        if (name.isBlank() || username.isBlank()) {
            return "Vui lòng nhập đầy đủ họ tên và tên đăng nhập!"
        }

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
