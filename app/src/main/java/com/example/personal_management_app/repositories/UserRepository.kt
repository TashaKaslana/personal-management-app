package com.example.personal_management_app.repositories

import com.example.personal_management_app.core.service.UserSession
import com.example.personal_management_app.entities.ProfileEntity
import java.util.Date
import java.util.UUID

class UserRepository {

    private data class DummyUser(
        val email: String,
        val password: String,
        val profile: ProfileEntity
    )

    private val users = mutableListOf(
        DummyUser(
            email = "duc.nguyen@example.com",
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

    fun login(email: String, password: String): UserSession? {
        val user = users.find { it.email == email.trim() && it.password == password } ?: return null
        return UserSession(
            userId = user.profile.id,
            token = UUID.randomUUID().toString(),
            username = user.profile.username
        )
    }

    fun register(name: String, email: String, password: String): UserSession? {
        if (users.any { it.email == email.trim() }) return null
        val profile = ProfileEntity(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            username = email.trim().substringBefore("@"),
            password = "••••••••",
            avatar = null,
            role = "Student",
            createdAt = Date(),
            updatedAt = Date(),
            deletedAt = null
        )
        users.add(DummyUser(email.trim(), password, profile))
        return UserSession(
            userId = profile.id,
            token = UUID.randomUUID().toString(),
            username = profile.username
        )
    }

    fun getProfile(): ProfileEntity {
        return users.first().profile
    }

    fun updateProfile(newName: String, newUsername: String) {
        val user = users.first()
        users[0] = user.copy(
            profile = user.profile.copy(
                name = newName,
                username = newUsername,
                updatedAt = Date()
            )
        )
    }
}
