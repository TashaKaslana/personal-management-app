package com.example.personal_management_app.mapper

import com.example.personal_management_app.dtos.NoteEditDto
import com.example.personal_management_app.entities.NoteEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteMapper @Inject constructor() {
    fun toEditDto(note: NoteEntity?): NoteEditDto? {
        return note?.let {
            NoteEditDto(
                id = it.id,
                title = it.title,
                titleStyle = it.titleStyle,
                content = it.content,
                contentStyle = it.contentStyle,
                tags = it.tags,
                backgroundColor = it.backgroundColor,
                isPinned = it.isPinned,
                archivedAt = it.archivedAt,
                reminder = it.reminder,
                deletedAt = it.deletedAt
            )
        }
    }

    fun toEntity(dto: NoteEditDto?): NoteEntity? {
        return dto?.let {
            NoteEntity(
                id = it.id,
                title = it.title,
                titleStyle = it.titleStyle,
                content = it.content,
                contentStyle = it.contentStyle,
                tags = it.tags,
                backgroundColor = it.backgroundColor,
                isPinned = it.isPinned,
                archivedAt = it.archivedAt,
                reminder = it.reminder,
                deletedAt = it.deletedAt
            )
        }
    }
}