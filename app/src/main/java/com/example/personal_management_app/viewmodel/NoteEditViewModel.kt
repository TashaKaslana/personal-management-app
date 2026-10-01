package com.example.personal_management_app.viewmodel

import javax.inject.Inject

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.personal_management_app.dtos.NoteEditDto
import com.example.personal_management_app.dtos.NoteTextStyle
import com.example.personal_management_app.mapper.NoteMapper
import com.example.personal_management_app.repositories.NoteRepository
import com.example.personal_management_app.ui.screen.note_screen.NoteBlock
import com.example.personal_management_app.ui.screen.note_screen.NoteCaret
import com.example.personal_management_app.ui.screen.note_screen.NoteHeading
import com.example.personal_management_app.ui.screen.note_screen.NoteInlineStyle
import com.example.personal_management_app.ui.screen.note_screen.NoteStyleTarget
import com.example.personal_management_app.ui.screen.note_screen.adjustSpans
import com.example.personal_management_app.ui.screen.note_screen.clearInline
import com.example.personal_management_app.ui.screen.note_screen.loadNoteContent
import com.example.personal_management_app.ui.screen.note_screen.loadTitleSpans
import com.example.personal_management_app.ui.screen.note_screen.newNoteBlockId
import com.example.personal_management_app.ui.screen.note_screen.saveNoteContent
import com.example.personal_management_app.ui.screen.note_screen.selectionHasStyle
import com.example.personal_management_app.ui.screen.note_screen.toggleInline
import kotlin.math.max
import kotlin.math.min
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val mapper: NoteMapper,
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val noteId: String? = savedStateHandle.get<String>("noteId")

    var newNote: NoteEditDto = NoteEditDto(
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

    var note: NoteEditDto? by mutableStateOf(
        if (noteId != null) checkNotNull(mapper.toEditDto(noteRepository.get(noteId))) else newNote
    )
        private set

    var blocks by mutableStateOf(loadNoteContent(note?.content.orEmpty()))
        private set

    var titleSpans by mutableStateOf(loadTitleSpans(note?.content.orEmpty()))
        private set

    var caret by mutableStateOf(NoteCaret())
        private set

    val showCheckbox: Boolean
        get() = blocks.any { it is NoteBlock.Checkbox }

    fun updateTitle(title: String, start: Int = title.length, end: Int = title.length) {
        val current = note ?: return
        caret = NoteCaret(NoteStyleTarget.Title, current.id, start, end)
        if (title == current.title) return
        titleSpans = adjustSpans(current.title, title, titleSpans)
        note = current.copy(title = title)
        commit(blocks)
    }

    fun updateContent(content: String) {
        titleSpans = loadTitleSpans(content)
        commit(loadNoteContent(content))
    }

    fun applyHeading(heading: NoteHeading) {
        val currentCaret = caret
        when (currentCaret.target) {
            NoteStyleTarget.Title -> {
                note = note?.copy(titleStyle = note?.titleStyle?.copy(fontSize = heading.fontSize) ?: return)
            }
            NoteStyleTarget.Text -> updateTextHeading(currentCaret.blockId, heading)
            NoteStyleTarget.Checkbox -> updateCheckboxHeading(currentCaret.blockId, heading)
            NoteStyleTarget.ModelTitle -> updateModelHeading(currentCaret.blockId, heading, title = true)
            NoteStyleTarget.ModelBody -> updateModelHeading(currentCaret.blockId, heading, title = false)
            NoteStyleTarget.None -> Unit
        }
    }

    fun toggleInlineStyle(style: NoteInlineStyle) {
        val currentCaret = caret
        val start = min(currentCaret.start, currentCaret.end)
        val end = max(currentCaret.start, currentCaret.end)
        if (start == end) return
        when (currentCaret.target) {
            NoteStyleTarget.Title -> {
                val current = note ?: return
                titleSpans = toggleInline(current.title.length, titleSpans, start, end, style)
                commit(blocks)
            }
            NoteStyleTarget.Text -> mapText(currentCaret.blockId) { block ->
                block.copy(spans = toggleInline(block.text.length, block.spans, start, end, style))
            }
            NoteStyleTarget.Checkbox -> mapCheckbox(currentCaret.blockId) { block ->
                block.copy(spans = toggleInline(block.label.length, block.spans, start, end, style))
            }
            NoteStyleTarget.ModelTitle -> mapModel(currentCaret.blockId) { block ->
                block.copy(titleSpans = toggleInline(block.title.length, block.titleSpans, start, end, style))
            }
            NoteStyleTarget.ModelBody -> mapModel(currentCaret.blockId) { block ->
                block.copy(bodySpans = toggleInline(block.body.length, block.bodySpans, start, end, style))
            }
            NoteStyleTarget.None -> Unit
        }
    }

    fun removeFormat() {
        val currentCaret = caret
        val start = min(currentCaret.start, currentCaret.end)
        val end = max(currentCaret.start, currentCaret.end)
        if (start != end) {
            clearSelection(currentCaret, start, end)
            return
        }
        when (currentCaret.target) {
            NoteStyleTarget.Title -> {
                note = note?.copy(
                    titleStyle = note?.titleStyle?.copy(
                        fontSize = NoteHeading.Normal.fontSize,
                        bold = false,
                        italic = false,
                        underline = false
                    ) ?: return
                )
                titleSpans = emptyList()
                commit(blocks)
            }
            NoteStyleTarget.Text -> mapText(currentCaret.blockId) { block ->
                block.copy(heading = NoteHeading.Normal, spans = emptyList())
            }
            NoteStyleTarget.Checkbox -> mapCheckbox(currentCaret.blockId) { block ->
                block.copy(heading = NoteHeading.Normal, spans = emptyList())
            }
            NoteStyleTarget.ModelTitle -> mapModel(currentCaret.blockId) { block ->
                block.copy(titleHeading = NoteHeading.Normal, titleSpans = emptyList())
            }
            NoteStyleTarget.ModelBody -> mapModel(currentCaret.blockId) { block ->
                block.copy(bodyHeading = NoteHeading.Normal, bodySpans = emptyList())
            }
            NoteStyleTarget.None -> Unit
        }
    }

    fun focusedHeading(): NoteHeading? {
        return when (caret.target) {
            NoteStyleTarget.Title -> when (note?.titleStyle?.fontSize) {
                NoteHeading.H1.fontSize -> NoteHeading.H1
                NoteHeading.H2.fontSize -> NoteHeading.H2
                NoteHeading.Normal.fontSize -> NoteHeading.Normal
                else -> null
            }
            NoteStyleTarget.Text -> blocks.filterIsInstance<NoteBlock.Text>()
                .find { it.id == caret.blockId }?.heading
            NoteStyleTarget.Checkbox -> blocks.filterIsInstance<NoteBlock.Checkbox>()
                .find { it.id == caret.blockId }?.heading
            NoteStyleTarget.ModelTitle -> blocks.filterIsInstance<NoteBlock.ModelBox>()
                .find { it.id == caret.blockId }?.titleHeading
            NoteStyleTarget.ModelBody -> blocks.filterIsInstance<NoteBlock.ModelBox>()
                .find { it.id == caret.blockId }?.bodyHeading
            NoteStyleTarget.None -> null
        }
    }

    fun selectionHas(style: NoteInlineStyle): Boolean {
        val start = min(caret.start, caret.end)
        val end = max(caret.start, caret.end)
        return when (caret.target) {
            NoteStyleTarget.Title -> selectionHasStyle(note?.title?.length ?: 0, titleSpans, start, end, style)
            NoteStyleTarget.Text -> blocks.filterIsInstance<NoteBlock.Text>()
                .find { it.id == caret.blockId }
                ?.let { selectionHasStyle(it.text.length, it.spans, start, end, style) } == true
            NoteStyleTarget.Checkbox -> blocks.filterIsInstance<NoteBlock.Checkbox>()
                .find { it.id == caret.blockId }
                ?.let { selectionHasStyle(it.label.length, it.spans, start, end, style) } == true
            NoteStyleTarget.ModelTitle -> blocks.filterIsInstance<NoteBlock.ModelBox>()
                .find { it.id == caret.blockId }
                ?.let { selectionHasStyle(it.title.length, it.titleSpans, start, end, style) } == true
            NoteStyleTarget.ModelBody -> blocks.filterIsInstance<NoteBlock.ModelBox>()
                .find { it.id == caret.blockId }
                ?.let { selectionHasStyle(it.body.length, it.bodySpans, start, end, style) } == true
            NoteStyleTarget.None -> false
        }
    }

    fun updateTag(tag: String) {
        note = note?.copy(tag = tag)
    }

    fun updateBackground(background: String) {
        note = note?.copy(backgroundColor = background)
    }

    fun pin() {
        val currentNote = note ?: return

        note?.let { thisNote ->
            note = thisNote.copy(isPinned = !thisNote.isPinned)
        }

        if (noteId != null) {
            noteRepository.pin(noteId, !currentNote.isPinned)
        }
    }

    fun setNotification(cron: String? = "") {
        val currentNote = note ?: return

        note?.let { thisNote ->
            note = thisNote.copy(isNotification = !thisNote.isNotification)
            note = thisNote.copy(notificationCron = cron)
        }

        if (noteId != null) {
            noteRepository.setNotification(noteId, !currentNote.isNotification, cron ?: "")
        }
    }

    fun setArchived() {
        val currentNote = note ?: return

        note?.let { thisNote ->
            note = thisNote.copy(isArchived = !thisNote.isArchived)
        }

        if (noteId != null) {
            noteRepository.setArchived(noteId, currentNote.isArchived)
        }
    }

    fun upsertNote() {
        val entity = mapper.toEntity(note) ?: return

        if (noteId == null) {
            noteRepository.insert(entity)
        }

        noteRepository.update(entity)
    }


    fun delete() {
        if (noteId != null) {
            noteRepository.delete(noteId)
        }
        note = null
    }

    fun copy() {
        val currentNote = note
        val copied = mapper.toEntity(currentNote)?.copy(
            id = UUID.randomUUID().toString()
        ) ?: return

        noteRepository.insert(copied)
    }

    fun toggleShowCheckbox() {
        val next = if (showCheckbox) {
            blocks.map { block ->
                if (block is NoteBlock.Checkbox) {
                    NoteBlock.Text(block.id, block.label, block.heading, block.spans)
                } else {
                    block
                }
            }
        } else {
            blocks.flatMap { block ->
                when (block) {
                    is NoteBlock.Text -> {
                        val lines = block.text.lines().ifEmpty { listOf("") }
                        var offset = 0
                        lines.map { line ->
                            val lineSpans = block.spans.mapNotNull { span ->
                                val start = (span.start - offset).coerceAtLeast(0)
                                val end = (span.end - offset).coerceAtMost(line.length)
                                if (span.end <= offset || span.start >= offset + line.length || end <= start) {
                                    null
                                } else {
                                    span.copy(start = start, end = end)
                                }
                            }
                            offset += line.length + 1
                            NoteBlock.Checkbox(
                                id = newNoteBlockId(),
                                checked = false,
                                label = line,
                                heading = block.heading,
                                spans = lineSpans
                            )
                        }
                    }

                    else -> listOf(block)
                }
            }
        }
        commit(next)
    }

    fun addCheckbox() {
        val checkbox = NoteBlock.Checkbox(
            id = newNoteBlockId(),
            checked = false,
            label = ""
        )
        val trailingEmptyText = blocks.lastOrNull() as? NoteBlock.Text
        val next = if (trailingEmptyText != null && trailingEmptyText.text.isEmpty()) {
            blocks.dropLast(1) + checkbox + trailingEmptyText
        } else {
            blocks + checkbox + NoteBlock.Text(newNoteBlockId(), "")
        }
        commit(next)
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

    fun updateTextBlock(id: String, text: String, start: Int = text.length, end: Int = text.length) {
        caret = NoteCaret(NoteStyleTarget.Text, id, start, end)
        val current = blocks.filterIsInstance<NoteBlock.Text>().find { it.id == id } ?: return
        if (current.text == text) return
        mapText(id) { block ->
            block.copy(text = text, spans = adjustSpans(block.text, text, block.spans))
        }
    }

    fun setCheckboxChecked(id: String, checked: Boolean) {
        commit(blocks.map { block ->
            if (block is NoteBlock.Checkbox && block.id == id) block.copy(checked = checked) else block
        })
    }

    fun updateCheckboxLabel(id: String, label: String, start: Int = label.length, end: Int = label.length) {
        caret = NoteCaret(NoteStyleTarget.Checkbox, id, start, end)
        val current = blocks.filterIsInstance<NoteBlock.Checkbox>().find { it.id == id } ?: return
        if (current.label == label) return
        mapCheckbox(id) { block ->
            block.copy(label = label, spans = adjustSpans(block.label, label, block.spans))
        }
    }

    fun updateModelBox(
        id: String,
        title: String,
        body: String,
        field: NoteStyleTarget = NoteStyleTarget.ModelBody,
        start: Int = 0,
        end: Int = 0
    ) {
        caret = NoteCaret(field, id, start, end)
        val current = blocks.filterIsInstance<NoteBlock.ModelBox>().find { it.id == id } ?: return
        val unchanged = when (field) {
            NoteStyleTarget.ModelTitle -> current.title == title
            NoteStyleTarget.ModelBody -> current.body == body
            else -> current.title == title && current.body == body
        }
        if (unchanged) return
        mapModel(id) { block ->
            when (field) {
                NoteStyleTarget.ModelTitle -> block.copy(
                    title = title,
                    titleSpans = adjustSpans(block.title, title, block.titleSpans)
                )
                NoteStyleTarget.ModelBody -> block.copy(
                    body = body,
                    bodySpans = adjustSpans(block.body, body, block.bodySpans)
                )
                else -> block.copy(title = title, body = body)
            }
        }
    }

    private fun updateTextHeading(id: String, heading: NoteHeading) {
        mapText(id) { it.copy(heading = heading) }
    }

    private fun updateCheckboxHeading(id: String, heading: NoteHeading) {
        mapCheckbox(id) { it.copy(heading = heading) }
    }

    private fun updateModelHeading(id: String, heading: NoteHeading, title: Boolean) {
        mapModel(id) { block ->
            if (title) block.copy(titleHeading = heading) else block.copy(bodyHeading = heading)
        }
    }

    private fun clearSelection(currentCaret: NoteCaret, start: Int, end: Int) {
        when (currentCaret.target) {
            NoteStyleTarget.Title -> {
                val length = note?.title?.length ?: return
                titleSpans = clearInline(length, titleSpans, start, end)
                commit(blocks)
            }
            NoteStyleTarget.Text -> mapText(currentCaret.blockId) { block ->
                block.copy(spans = clearInline(block.text.length, block.spans, start, end))
            }
            NoteStyleTarget.Checkbox -> mapCheckbox(currentCaret.blockId) { block ->
                block.copy(spans = clearInline(block.label.length, block.spans, start, end))
            }
            NoteStyleTarget.ModelTitle -> mapModel(currentCaret.blockId) { block ->
                block.copy(titleSpans = clearInline(block.title.length, block.titleSpans, start, end))
            }
            NoteStyleTarget.ModelBody -> mapModel(currentCaret.blockId) { block ->
                block.copy(bodySpans = clearInline(block.body.length, block.bodySpans, start, end))
            }
            NoteStyleTarget.None -> Unit
        }
    }

    private fun mapText(id: String, transform: (NoteBlock.Text) -> NoteBlock.Text) {
        commit(blocks.map { block ->
            if (block is NoteBlock.Text && block.id == id) transform(block) else block
        })
    }

    private fun mapCheckbox(id: String, transform: (NoteBlock.Checkbox) -> NoteBlock.Checkbox) {
        commit(blocks.map { block ->
            if (block is NoteBlock.Checkbox && block.id == id) transform(block) else block
        })
    }

    private fun mapModel(id: String, transform: (NoteBlock.ModelBox) -> NoteBlock.ModelBox) {
        commit(blocks.map { block ->
            if (block is NoteBlock.ModelBox && block.id == id) transform(block) else block
        })
    }

    private fun commit(next: List<NoteBlock>) {
        blocks = next
        note = note?.copy(content = saveNoteContent(next, titleSpans))
    }
}