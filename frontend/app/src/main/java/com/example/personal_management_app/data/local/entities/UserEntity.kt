package com.example.personal_management_app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.personal_management_app.entities.ProfileEntity
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val password: String,
    val name: String,
    val username: String,
    val avatar: String? = null,
    val role: String = "Student",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
) {
    fun toProfileEntity(): ProfileEntity {
        return ProfileEntity(
            id = id,
            name = name,
            username = username,
            password = "••••••••",
            avatar = avatar,
            role = role,
            createdAt = Date(createdAt),
            updatedAt = Date(updatedAt),
            deletedAt = deletedAt?.let { Date(it) }
        )
    }
}
