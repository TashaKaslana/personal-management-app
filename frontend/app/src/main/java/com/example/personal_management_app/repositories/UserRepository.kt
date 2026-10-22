package com.example.personal_management_app.repositories

import com.example.personal_management_app.dtos.AuthResult
import com.example.personal_management_app.entities.ProfileEntity
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    private data class DummyUser(
        val id: String,
        val email: String,
        val password: String,
        val profile: ProfileEntity
    )

    private val users = mutableListOf(
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

    fun login(email: String, password: String): AuthResult {
        val user = users.find { it.email == email.trim() && it.password == password }
            ?: return AuthResult.WrongCredentials

        return AuthResult.Success(
            user = user.profile,
            token = UUID.randomUUID().toString()
        )
    }

    fun register(name: String, email: String, password: String): AuthResult {
        if (users.any { it.email == email.trim() }) {
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

        users.add(DummyUser(id, email.trim(), password, profile))

        return AuthResult.Success(
            user = profile,
            token = UUID.randomUUID().toString()
        )
    }

    fun changePassword(userId: String, currentPassword: String, newPassword: String): Boolean {
        val index = users.indexOfFirst { it.id == userId }
        if (index < 0 || users[index].password != currentPassword) return false
        users[index] = users[index].copy(password = newPassword)
        return true
    }

    fun getProfile(userId: String): ProfileEntity? {
        return users.find { it.id == userId }?.profile
    }

    fun updateProfile(userId: String, newName: String, newUsername: String): Boolean {
        val index = users.indexOfFirst { it.id == userId }
        if (index < 0) return false
        users[index] = users[index].copy(
            profile = users[index].profile.copy(
                name = newName,
                username = newUsername,
                updatedAt = Date()
            )
        )
        return true
    }
}
