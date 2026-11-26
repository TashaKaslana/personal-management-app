package com.example.personal_management_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "feedbacks")
data class FeedbackEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val feedbackType: String,
    val content: String,
    val deviceInfo: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
