package com.example.personal_management_app.dtos

import com.example.personal_management_app.entities.NoteReminder

data class NoteEditDto(
    val id: String,
    val title: String,
    val titleStyle: NoteTextStyle,
    val content: String,
    val contentStyle: NoteTextStyle,
    val tags: List<String> = emptyList(),
    val backgroundColor: String = "white",
    val isPinned: Boolean = false,
    val archivedAt: Long? = null,
    val reminder: NoteReminder? = null,
    val deletedAt: Long? = null
)