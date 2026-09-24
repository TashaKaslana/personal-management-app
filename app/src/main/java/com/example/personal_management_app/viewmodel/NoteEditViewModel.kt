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
import com.example.personal_management_app.ui.screen.note_screen.NoteBlock
import com.example.personal_management_app.ui.screen.note_screen.loadNoteContent
import com.example.personal_management_app.ui.screen.note_screen.newNoteBlockId
import com.example.personal_management_app.ui.screen.note_screen.saveNoteContent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID

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

    var blocks by mutableStateOf(loadNoteContent(note?.content.orEmpty()))
        private set

    val showCheckbox: Boolean
        get() = blocks.any { it is NoteBlock.Checkbox }

    fun updateTitle(title: String) {
        note = note?.copy(title = title)
    }

    fun updateContent(content: String) {
        commit(loadNoteContent(content))
    }

    fun updateTag(tag: String) {
        note = note?.copy(tag = tag)
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

    fun delete() {
        noteRepository.delete(noteId)
        note = null
    }

    fun copy() {
        val currentNote = note ?: return
        val copied = mapper.toEntity(currentNote)?.copy(
            id = UUID.randomUUID().toString()
        ) ?: return

        noteRepository.insert(copied)
    }

    fun toggleShowCheckbox() {
        val next = if (showCheckbox) {
            blocks.map { block ->
                if (block is NoteBlock.Checkbox) {
                    NoteBlock.Text(block.id, block.label)
                } else {
                    block
                }
            }
        } else {
            blocks.flatMap { block ->
                when (block) {
                    is NoteBlock.Text -> block.text.lines().ifEmpty { listOf("") }.map { line ->
                        NoteBlock.Checkbox(
                            id = newNoteBlockId(),
                            checked = false,
                            label = line
                        )
                    }
                    else -> listOf(block)
                }
            }
        }
        commit(next)
    }

    fun addCheckbox() {
        commit(
            blocks + NoteBlock.Checkbox(
                id = newNoteBlockId(),
                checked = false,
                label = ""
            )
        )
    }

    fun addImage(uri: String) {
        if (uri.isBlank()) return
        commit(blocks + NoteBlock.Image(id = newNoteBlockId(), uri = uri))
    }

    fun addModelBox() {
        commit(
            blocks + NoteBlock.ModelBox(
                id = newNoteBlockId(),
                title = "",
                body = ""
            )
        )
    }

    fun updateTextBlock(id: String, text: String) {
        commit(blocks.map { block ->
            if (block is NoteBlock.Text && block.id == id) block.copy(text = text) else block
        })
    }

    fun setCheckboxChecked(id: String, checked: Boolean) {
        commit(blocks.map { block ->
            if (block is NoteBlock.Checkbox && block.id == id) block.copy(checked = checked) else block
        })
    }

    fun updateCheckboxLabel(id: String, label: String) {
        commit(blocks.map { block ->
            if (block is NoteBlock.Checkbox && block.id == id) block.copy(label = label) else block
        })
    }

    fun updateModelBox(id: String, title: String, body: String) {
        commit(blocks.map { block ->
            if (block is NoteBlock.ModelBox && block.id == id) {
                block.copy(title = title, body = body)
            } else {
                block
            }
        })
    }

    private fun commit(next: List<NoteBlock>) {
        blocks = next
        note = note?.copy(content = saveNoteContent(next))
    }
}