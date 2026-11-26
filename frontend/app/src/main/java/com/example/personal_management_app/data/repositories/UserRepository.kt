package com.example.personal_management_app.data.repositories

import com.example.personal_management_app.data.local.dao.UserDao
import com.example.personal_management_app.data.local.entities.UserEntity
import com.example.personal_management_app.dtos.AuthResult
import com.example.personal_management_app.data.entities.ProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.personal_management_app.data.remote.*
import java.io.IOException
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val api: BackendApi
) {
    suspend fun login(email: String, password: String): AuthResult {
        val response = api.auth.login(LoginRequest(email.trim(), password))
        if (response.code() == 401) return AuthResult.WrongCredentials
        return cacheSession(response.payload())
    }

    suspend fun register(name: String, email: String, password: String): AuthResult {
        val response = api.auth.register(RegisterRequest(name.trim(), email.trim(), password))
        if (response.code() == 409) return AuthResult.EmailTaken
        return cacheSession(response.payload())
    }

    private fun cacheSession(auth: AuthResponse): AuthResult {
        val user = cacheUser(auth.user ?: throw IOException("Missing user in auth response"))
        val token = auth.token ?: throw IOException("Missing session token")
        return AuthResult.Success(user.toProfileEntity(), token)
    }

    private fun cacheUser(dto: UserDto): UserEntity {
        val id = dto.id ?: throw IOException("Missing user id")
        val user = UserEntity(
            id = id,
            email = dto.email.orEmpty(),
            password = "",
            name = dto.name.orEmpty(),
            username = dto.username.orEmpty(),
            avatar = userDao.getUserById(id)?.avatar ?: dto.avatarUrl,
            role = dto.role?.name.orEmpty(),
            createdAt = dto.createdAt?.let { Instant.parse(it).toEpochMilli() }
                ?: System.currentTimeMillis(),
            updatedAt = dto.updatedAt?.let { Instant.parse(it).toEpochMilli() }
                ?: System.currentTimeMillis()
        )
        userDao.insertUser(user)
        return user
    }

    suspend fun changePassword(userId: String, currentPassword: String, newPassword: String): Boolean {
        api.user.updateUserPassword(userId,
            ChangePasswordRequest(currentPassword, newPassword)).payload()
        return true
    }

    fun getProfile(userId: String): ProfileEntity? {
        return userDao.getUserById(userId)?.toProfileEntity()
    }

    fun getProfileFlow(userId: String): Flow<ProfileEntity?> {
        return userDao.getUserFlow(userId).map { it?.toProfileEntity() }
    }

    suspend fun updateProfile(
        userId: String,
        newName: String,
        newUsername: String
    ): Boolean {
        cacheUser(api.user.updateUser(userId,
            UpdateUserRequest(name = newName, username = newUsername))
            .payload())
        return true
    }

    fun updateAvatar(userId: String, avatarPath: String?): Boolean {
        val user = userDao.getUserById(userId) ?: return false
        userDao.updateAvatar(userId, avatarPath)
        return true
    }
}
