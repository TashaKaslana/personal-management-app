package com.example.personal_management_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personal_management_app.entites.NoteEntity
import com.example.personal_management_app.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
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

    fun filterNotes(archived: Boolean, query: String = ""): List<NoteEntity> {
        return _notes.value.filter { note ->
            (note.archivedAt != null) == archived && (                query.isBlank() ||
                    note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true) ||
                    note.tags.any { it.contains(query, ignoreCase = true) }
                )
        }
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
}