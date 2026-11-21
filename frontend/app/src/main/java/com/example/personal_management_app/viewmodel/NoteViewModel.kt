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

    private val _trashNotes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val trashNotes = _trashNotes.asStateFlow()

    init {
        loadNotes()
        viewModelScope.launch {
            repository.getNotesFlow().collect { list ->
                _notes.value = list
            }
        }
        viewModelScope.launch {
            repository.getTrashFlow().collect { list ->
                _trashNotes.value = list
            }
        }
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

    fun filterNotes(archived: Boolean, query: String = ""): List<NoteEntity> {
        return _notes.value.filter { note ->
            (note.archivedAt != null) == archived && note.matchesQuery(query)
        }
    }

    fun filterReminders(query: String = ""): List<NoteEntity> {
        return _notes.value.filter { note ->
            note.reminder != null && note.archivedAt == null && note.matchesQuery(query)
        }.sortedBy { it.reminder?.atMillis }
    }

    fun getNote(noteId: String): NoteEntity? {
        return repository.get(noteId)
    }

    fun filterTrash(query: String = ""): List<NoteEntity> {
        val list = _trashNotes.value
        return if (query.isBlank()) {
            list
        } else {
            list.filter { it.matchesQuery(query) }
        }
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
            reminderScheduler.cancel(noteId)
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
            reminderScheduler.cancel(noteId)
            loadNotes()
        }
    }
}
