package com.example.personal_management_app.entites

import androidx.compose.ui.graphics.Color

data class ArchiveEntity(
    val id: String,
    val title: String,
    val content: String,
    val tag: String? = null,
    val timeOrStatusText: String,
    val backgroundColor: Color,
    val isCheckList: Boolean = false
)