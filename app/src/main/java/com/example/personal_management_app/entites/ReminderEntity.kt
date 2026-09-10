package com.example.personal_management_app.entites

import androidx.compose.ui.graphics.Color

data class ReminderEntity(
    val id: String,
    val title: String,
    val content: String,
    val timeText: String,
    val backgroundColor: Color,
    val isCheckList: Boolean = false
)