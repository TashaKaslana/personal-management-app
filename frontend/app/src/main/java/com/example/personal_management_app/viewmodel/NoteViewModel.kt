package com.example.personal_management_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personal_management_app.entities.NoteEntity
import com.example.personal_management_app.repositories.NoteRepository
import com.example.personal_management_app.ui.screen.reminder.ReminderScheduler
import com.example.personal_management_app.utils.matchesQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {
    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notes = _notes.asStateFlow()

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            _notes.value = repository.getList()
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.update(note)
            loadNotes()
        }
    }

    fun filterNotes(
        archived: Boolean,
        query: String = "",
        selectedTag: String? = null
    ): List<NoteEntity> {
        return _notes.value.filter { note ->
            (note.archivedAt != null) == archived &&
            note.deletedAt == null &&
            (selectedTag.isNullOrBlank() || note.tags.contains(selectedTag)) &&
            note.matchesQuery(query)
        }
    }

    fun filterReminders(
        query: String = "",
        selectedTag: String? = null
    ): List<NoteEntity> {
        return _notes.value.filter { note ->
            note.reminder != null &&
            note.archivedAt == null &&
            note.deletedAt == null &&
            (selectedTag.isNullOrBlank() || note.tags.contains(selectedTag)) &&
            note.matchesQuery(query)
        }.sortedBy { it.reminder?.atMillis }
    }

    fun getNote(noteId: String): NoteEntity? {
        return repository.get(noteId)
    }

    fun filterTrash(query: String = ""): List<NoteEntity> {
        return repository.getTrashItems(query)
    }

    fun restoreNote(noteId: String) {
        viewModelScope.launch {
            repository.restore(noteId)
            loadNotes()
        }
    }

    fun deleteNoteForever(noteId: String) {
        viewModelScope.launch {
            repository.deleteForever(noteId)
            loadNotes()
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            repository.emptyTrash()
            loadNotes()
        }
    }

    fun addNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.insert(note)
            loadNotes()
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.delete(noteId)
            loadNotes()
        }
    }

    fun setArchive(noteId: String, isArchived: Boolean) {
        viewModelScope.launch {
            val note = repository.get(noteId) ?: return@launch
            val updated = note.copy(
                archivedAt = if (isArchived) System.currentTimeMillis() else null,
                isPinned = if (isArchived) false else note.isPinned
            )
            repository.update(updated)
            loadNotes()
        }
    }

    fun togglePin(noteId: String) {
        viewModelScope.launch {
            val note = repository.get(noteId) ?: return@launch
            val updated = note.copy(isPinned = !note.isPinned)
            repository.update(updated)
            loadNotes()
        }
    }
}
