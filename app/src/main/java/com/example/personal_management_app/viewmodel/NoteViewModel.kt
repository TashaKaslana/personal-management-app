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

    fun getNote(noteId: String): NoteEntity? {
        return repository.get(noteId)
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