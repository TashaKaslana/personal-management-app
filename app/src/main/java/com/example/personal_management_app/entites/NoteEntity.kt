package com.example.personal_management_app.entites

import com.example.personal_management_app.dtos.NoteTextStyle


data class NoteEntity(
    val id: String,
    val title: String,
    val titleStyle: NoteTextStyle,
    val content: String,
    val contentStyle: NoteTextStyle,
    val tags: List<String> = emptyList(),
    val backgroundColor: String,
    val isPinned: Boolean = false,
    val archivedAt: Long? = null,
    val deletedAt: Long? = null,
    val reminder: NoteReminder? = null
)
