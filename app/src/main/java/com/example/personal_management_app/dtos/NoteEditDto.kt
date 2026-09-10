package com.example.personal_management_app.dtos

import kotlin.String

data class NoteEditDto(
    val id: String,
    val title: String,
    val titleStyle: NoteTextStyle,
    val content: String,
    val contentStyle: NoteTextStyle,
    val tag: String? = null,
    val backgroundColor: String = "white",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isNotification: Boolean = false,
    val notificationCron: String? = null
)