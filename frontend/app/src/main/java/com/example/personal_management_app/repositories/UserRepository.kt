package com.example.personal_management_app.repositories

import android.content.Context
import com.example.personal_management_app.dtos.AuthResult
import com.example.personal_management_app.entities.ProfileEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private data class DummyUser(
        val id: String,
        val email: String,
        val password: String,
        val profile: ProfileEntity
    )

    private val initialUsers = mutableListOf(
        DummyUser(
            id = "user_01",
            email = "abc@gmail.com",
            password = "password123",
            profile = ProfileEntity(
                id = "user_01",
                name = "Nguyễn Minh Đức",
                username = "BiOwO",
                password = "••••••••",
                avatar = null,
                role = "Student / Developer",
                createdAt = Date(),
                updatedAt = Date(),
                deletedAt = null
            )
        )
    )

    private val _users = MutableStateFlow<List<DummyUser>>(loadInitialUsers())

    private fun loadInitialUsers(): List<DummyUser> {
        val list = initialUsers.toMutableList()
        val savedName = sharedPrefs.getString("user_01_name", null)
        val savedUsername = sharedPrefs.getString("user_01_username", null)
        val savedAvatar = sharedPrefs.getString("user_01_avatar", null)
        val savedPassword = sharedPrefs.getString("user_01_password", null)

        val idx = list.indexOfFirst { it.id == "user_01" }
        if (idx >= 0) {
            val user = list[idx]
            list[idx] = user.copy(
                password = savedPassword ?: user.password,
                profile = user.profile.copy(
                    name = savedName ?: user.profile.name,
                    username = savedUsername ?: user.profile.username,
                    avatar = savedAvatar ?: user.profile.avatar
                )
            )
        }
        return list
    }

    private fun saveUserToPrefs(user: DummyUser) {
        sharedPrefs.edit().apply {
            putString("${user.id}_name", user.profile.name)
            putString("${user.id}_username", user.profile.username)
            putString("${user.id}_avatar", user.profile.avatar)
            putString("${user.id}_password", user.password)
            apply()
        }
    }

    fun login(email: String, password: String): AuthResult {
        val user = _users.value.find { it.email == email.trim() && it.password == password }
            ?: return AuthResult.WrongCredentials

        return AuthResult.Success(
            user = user.profile,
            token = UUID.randomUUID().toString()
        )
    }

    fun register(name: String, email: String, password: String): AuthResult {
        if (_users.value.any { it.email == email.trim() }) {
            return AuthResult.EmailTaken
        }

        val id = UUID.randomUUID().toString()
        val profile = ProfileEntity(
            id = id,
            name = name.trim(),
            username = email.trim().substringBefore("@"),
            password = "••••••••",
            avatar = null,
            role = "Student",
            createdAt = Date(),
            updatedAt = Date(),
            deletedAt = null
        )

        val newUser = DummyUser(id, email.trim(), password, profile)
        val updatedList = _users.value.toMutableList().apply { add(newUser) }
        _users.value = updatedList
        saveUserToPrefs(newUser)

        return AuthResult.Success(
            user = profile,
            token = UUID.randomUUID().toString()
        )
    }

    fun changePassword(userId: String, currentPassword: String, newPassword: String): Boolean {
        val currentList = _users.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == userId }
        if (index < 0 || currentList[index].password != currentPassword) return false
        val updatedUser = currentList[index].copy(password = newPassword)
        currentList[index] = updatedUser
        _users.value = currentList
        saveUserToPrefs(updatedUser)
        return true
    }

    fun getProfile(userId: String): ProfileEntity? {
        return _users.value.find { it.id == userId }?.profile
    }

    fun getProfileFlow(userId: String): Flow<ProfileEntity?> {
        return _users.map { list -> list.find { it.id == userId }?.profile }
    }

    fun updateProfile(
        userId: String,
        newName: String,
        newUsername: String,
        newAvatar: String? = null,
        keepExistingAvatarIfNull: Boolean = true
    ): Boolean {
        val currentList = _users.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == userId }
        if (index < 0) return false

        val currentProfile = currentList[index].profile
        val updatedAvatar = if (keepExistingAvatarIfNull && newAvatar == null) {
            currentProfile.avatar
        } else {
            newAvatar
        }

        val updatedUser = currentList[index].copy(
            profile = currentProfile.copy(
                name = newName,
                username = newUsername,
                avatar = updatedAvatar,
                updatedAt = Date()
            )
        )
        currentList[index] = updatedUser
        _users.value = currentList
        saveUserToPrefs(updatedUser)
        return true
    }

    fun updateAvatar(userId: String, avatarPath: String?): Boolean {
        val currentList = _users.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == userId }
        if (index < 0) return false

        val updatedUser = currentList[index].copy(
            profile = currentList[index].profile.copy(
                avatar = avatarPath,
                updatedAt = Date()
            )
        )
        currentList[index] = updatedUser
        _users.value = currentList
        saveUserToPrefs(updatedUser)
        return true
    }
}
