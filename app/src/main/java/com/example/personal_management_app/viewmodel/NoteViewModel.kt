package com.example.personal_management_app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.personal_management_app.entites.NoteEntity
import com.example.personal_management_app.repositories.NoteRepository

class NoteViewModel : ViewModel() {
    private val repository = NoteRepository()

    // Compose state list to automatically trigger UI redraws on change
    val notes: List<NoteEntity>
        field = mutableStateListOf<NoteEntity>().apply {
            addAll(repository.getList())
        }

    fun getNote(noteId: String): NoteEntity? {
        return repository.get(noteId)
    }

    fun addNote(note: NoteEntity) {
        repository.insert(note)
        refreshNotes()
    }

    fun updateNote(note: NoteEntity) {
        repository.update(note)
        refreshNotes()
    }

    fun deleteNote(noteId: String) {
        repository.delete(noteId)
        refreshNotes()
    }

    private fun refreshNotes() {
        notes.clear()
        notes.addAll(repository.getList())
    }
}