package com.example.personal_management_app.mapper

import com.example.personal_management_app.dtos.NoteEditDto
import com.example.personal_management_app.entites.NoteEntity
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
                tag = it.tag,
                backgroundColor = it.backgroundColor,
                isPinned = it.isPinned,
                isArchived = it.isArchived,
                isNotification = it.isNotification,
                notificationCron = it.notificationCron
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
                tag = it.tag,
                backgroundColor = it.backgroundColor,
                isPinned = it.isPinned,
                isArchived = it.isArchived,
                isNotification = it.isNotification,
                notificationCron = it.notificationCron
            )
        }
    }
}