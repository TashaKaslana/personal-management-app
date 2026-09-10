package com.example.personal_management_app.viewmodel

import javax.inject.Inject

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.personal_management_app.dtos.NoteEditDto
import com.example.personal_management_app.mapper.NoteMapper
import com.example.personal_management_app.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val mapper: NoteMapper,
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val noteId: String =
        checkNotNull(savedStateHandle["noteId"])

    var note: NoteEditDto? by mutableStateOf(
        mapper.toEditDto(noteRepository.get(noteId))
    )
        private set

    fun updateTitle(title: String) {
        note = note?.copy(title = title)
    }

    fun updateContent(content: String) {
        note = note?.copy(content = content)
    }

    fun pin() {
        val currentNote = note ?: return

        noteRepository.pin(noteId, !currentNote.isPinned)
    }

    fun setNotification(cron: String? = "") {
        val currentNote = note ?: return

        noteRepository.setNotification(noteId, !currentNote.isNotification, cron ?: "")
    }

    fun setArchived() {
        val currentNote = note ?: return

        noteRepository.setArchived(noteId, currentNote.isArchived)
    }

    fun updateNote() {
        val entity = mapper.toEntity(note)

        if (entity != null) {
            noteRepository.update(entity)
        }
    }
}