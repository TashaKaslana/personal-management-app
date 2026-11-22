package com.example.personal_management_app.repositories

import com.example.personal_management_app.data.local.dao.TagDao
import com.example.personal_management_app.data.local.dao.UserDao
import com.example.personal_management_app.data.local.entities.UserEntity
import com.example.personal_management_app.dtos.AuthResult
import com.example.personal_management_app.entities.ProfileEntity
import com.example.personal_management_app.entities.TagEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val tagDao: TagDao
) {
    fun login(email: String, password: String): AuthResult {
        val user = userDao.getUserByEmail(email.trim())
            ?: return AuthResult.WrongCredentials

        if (user.password != password) {
            return AuthResult.WrongCredentials
        }

        return AuthResult.Success(
            user = user.toProfileEntity(),
            token = UUID.randomUUID().toString()
        )
    }

    fun register(name: String, email: String, password: String): AuthResult {
        if (userDao.getUserByEmail(email.trim()) != null) {
            return AuthResult.EmailTaken
        }

        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val user = UserEntity(
            id = id,
            email = email.trim(),
            password = password,
            name = name.trim(),
            username = email.trim().substringBefore("@"),
            avatar = null,
            role = "Student",
            createdAt = now,
            updatedAt = now,
            deletedAt = null
        )

        userDao.insertUser(user)

        // Seed brand-new default tags for this new user:
        tagDao.insertAll(
            listOf(
                TagEntity(id = UUID.randomUUID().toString(), userId = id, name = "Công việc", color = "0xFF64B5F6"),
                TagEntity(id = UUID.randomUUID().toString(), userId = id, name = "Du lịch", color = "0xFF4DB6AC")
            )
        )

        return AuthResult.Success(
            user = user.toProfileEntity(),
            token = UUID.randomUUID().toString()
        )
    }

    fun changePassword(userId: String, currentPassword: String, newPassword: String): Boolean {
        val user = userDao.getUserById(userId) ?: return false
        if (user.password != currentPassword) return false
        userDao.updatePassword(userId, newPassword)
        return true
    }

    fun getProfile(userId: String): ProfileEntity? {
        return userDao.getUserById(userId)?.toProfileEntity()
    }

    fun getProfileFlow(userId: String): Flow<ProfileEntity?> {
        return userDao.getUserFlow(userId).map { it?.toProfileEntity() }
    }

    fun updateProfile(
        userId: String,
        newName: String,
        newUsername: String,
        newAvatar: String? = null,
        keepExistingAvatarIfNull: Boolean = true
    ): Boolean {
        val user = userDao.getUserById(userId) ?: return false
        val avatar = if (keepExistingAvatarIfNull && newAvatar == null) user.avatar else newAvatar
        userDao.updateProfile(userId, newName, newUsername, avatar)
        return true
    }

    fun updateAvatar(userId: String, avatarPath: String?): Boolean {
        val user = userDao.getUserById(userId) ?: return false
        userDao.updateAvatar(userId, avatarPath)
        return true
    }
}
