package com.example.personal_management_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.personal_management_app.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserFlow(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Update
    fun updateUser(user: UserEntity)

    @Query("UPDATE users SET password = :newPassword, updatedAt = :updatedAt WHERE id = :userId")
    fun updatePassword(userId: String, newPassword: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE users SET avatar = :avatarPath, updatedAt = :updatedAt WHERE id = :userId")
    fun updateAvatar(userId: String, avatarPath: String?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE users SET name = :name, username = :username, avatar = COALESCE(:avatar, avatar), updatedAt = :updatedAt WHERE id = :userId")
    fun updateProfile(userId: String, name: String, username: String, avatar: String?, updatedAt: Long = System.currentTimeMillis())
}
