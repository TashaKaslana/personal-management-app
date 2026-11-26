package com.example.personal_management_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.personal_management_app.dtos.NoteTextStyle

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val userId: String = "",
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
