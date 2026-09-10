package com.example.personal_management_app.entites

import com.example.personal_management_app.dtos.NoteTextStyle


data class NoteEntity(
    val id: String,
    val title: String,
    val titleStyle: NoteTextStyle,
    val content: String,
    val contentStyle: NoteTextStyle,
    val tag: String? = null,
    val backgroundColor: String,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isNotification: Boolean = false,
    val notificationCron: String? = null
)
