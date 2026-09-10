package com.example.personal_management_app.repositories

import com.example.personal_management_app.entites.ProfileEntity
import java.util.Date

class ProfileRepository {

    private var currentProfile = ProfileEntity(
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

    fun getProfile(): ProfileEntity {
        return currentProfile
    }

    fun updateProfile(newName: String, newUsername: String) {
        currentProfile = currentProfile.copy(
            name = newName,
            username = newUsername,
            updatedAt = Date()
        )
    }
}