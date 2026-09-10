package com.example.personal_management_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.personal_management_app.dtos.NoteEditDto
import com.example.personal_management_app.dtos.NoteTextStyle
import com.example.personal_management_app.mapper.NoteMapper
import com.example.personal_management_app.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import java.util.UUID

@HiltViewModel
class NoteAddViewModel @Inject constructor(
    private val mapper: NoteMapper,
    private val noteRepository: NoteRepository,
): ViewModel() {

    var note: NoteEditDto = NoteEditDto(
        id = UUID.randomUUID().toString(),
        title = "Untitled",
        titleStyle = NoteTextStyle(
            fontSize = 24f,
            bold = true
        ),
        content = "",
        contentStyle = NoteTextStyle(
            fontSize = 16f
        ),
        backgroundColor = "0xFFFFF8D6"
    )
        private set

    fun updateTitle(title: String) {
        note = note.copy(title = title)
    }

    fun updateContent(content: String) {
        note = note.copy(content = content)
    }

    fun pin() {
        noteRepository.pin(note.id, !note.isPinned)
    }

    fun setNotification(cron: String? = "") {
        noteRepository.setNotification(note.id, !note.isNotification, cron ?: "")
    }

    fun setArchived() {
        noteRepository.setArchived(note.id, note.isArchived)
    }

    fun addNote() {
        val entity = mapper.toEntity(note)
        if (entity != null) {
            noteRepository.insert(entity)
        }
    }
}