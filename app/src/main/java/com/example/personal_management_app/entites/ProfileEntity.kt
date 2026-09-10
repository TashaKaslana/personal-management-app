package com.example.personal_management_app.entites

import java.util.Date

data class ProfileEntity(
    val id: String,
    val name: String,
    val username: String,
    val password: String,
    val avatar: String? = null,
    val role: String,
    val createdAt: Date,
    val updatedAt: Date,
    val deletedAt: Date? = null
)