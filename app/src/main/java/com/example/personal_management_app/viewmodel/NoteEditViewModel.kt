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
import com.example.personal_management_app.ui.screen.note_screen.NotePanelType
import com.example.personal_management_app.ui.screen.note_screen.NoteStyleTarget
import com.example.personal_management_app.ui.screen.note_screen.adjustSpans
import com.example.personal_management_app.ui.screen.note_screen.clearInline
import com.example.personal_management_app.ui.screen.note_screen.containsBlock
import com.example.personal_management_app.ui.screen.note_screen.flattenBlocks
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

    /** Panel that should grab focus once, right after it got inserted. */
    var pendingPanelFocusId by mutableStateOf<String?>(null)
        private set

    /** Every block of the note, panels included, flattened depth first. */
    private val allBlocks: List<NoteBlock>
        get() = blocks.flattenBlocks()

    val showCheckbox: Boolean
        get() = allBlocks.any { it is NoteBlock.Checkbox }

    /** Innermost panel holding the caret, null when the caret sits outside of any panel. */
    fun focusedPanelId(): String? {
        if (caret.blockId.isBlank()) return null
        return allBlocks
            .filterIsInstance<NoteBlock.ModelBox>()
            .lastOrNull { it.containsBlock(caret.blockId) }
            ?.id
    }

    fun panelType(id: String): NotePanelType? =
        allBlocks.filterIsInstance<NoteBlock.ModelBox>().find { it.id == id }?.type

    fun panelBackground(id: String): String? =
        allBlocks.filterIsInstance<NoteBlock.ModelBox>().find { it.id == id }?.backgroundColor

    fun consumePanelFocus() {
        pendingPanelFocusId = null
    }

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
            NoteStyleTarget.PanelTitle -> updatePanelHeading(currentCaret.blockId, heading)
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
            NoteStyleTarget.PanelTitle -> mapPanel(currentCaret.blockId) { block ->
                block.copy(titleSpans = toggleInline(block.title.length, block.titleSpans, start, end, style))
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
            NoteStyleTarget.PanelTitle -> mapPanel(currentCaret.blockId) { block ->
                block.copy(titleHeading = NoteHeading.Normal, titleSpans = emptyList())
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
            NoteStyleTarget.Text -> allBlocks.filterIsInstance<NoteBlock.Text>()
                .find { it.id == caret.blockId }?.heading
            NoteStyleTarget.Checkbox -> allBlocks.filterIsInstance<NoteBlock.Checkbox>()
                .find { it.id == caret.blockId }?.heading
            NoteStyleTarget.PanelTitle -> allBlocks.filterIsInstance<NoteBlock.ModelBox>()
                .find { it.id == caret.blockId }?.titleHeading
            NoteStyleTarget.None -> null
        }
    }

    fun selectionHas(style: NoteInlineStyle): Boolean {
        val start = min(caret.start, caret.end)
        val end = max(caret.start, caret.end)
        return when (caret.target) {
            NoteStyleTarget.Title -> selectionHasStyle(note?.title?.length ?: 0, titleSpans, start, end, style)
            NoteStyleTarget.Text -> allBlocks.filterIsInstance<NoteBlock.Text>()
                .find { it.id == caret.blockId }
                ?.let { selectionHasStyle(it.text.length, it.spans, start, end, style) } == true
            NoteStyleTarget.Checkbox -> allBlocks.filterIsInstance<NoteBlock.Checkbox>()
                .find { it.id == caret.blockId }
                ?.let { selectionHasStyle(it.label.length, it.spans, start, end, style) } == true
            NoteStyleTarget.PanelTitle -> allBlocks.filterIsInstance<NoteBlock.ModelBox>()
                .find { it.id == caret.blockId }
                ?.let { selectionHasStyle(it.title.length, it.titleSpans, start, end, style) } == true
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
            blocks.map { block -> block.toPlainText() }
        } else {
            blocks.flatMap { block -> block.toCheckboxes() }
        }
        commit(next)
    }

    fun addCheckbox() {
        val checkbox = NoteBlock.Checkbox(
            id = newNoteBlockId(),
            checked = false,
            label = ""
        )
        insertAtCaret(checkbox, keepTrailingText = true)
        caret = NoteCaret(NoteStyleTarget.Checkbox, checkbox.id, 0, 0)
    }

    fun addImage(uri: String) {
        if (uri.isBlank()) return
        insertAtCaret(NoteBlock.Image(id = newNoteBlockId(), uri = uri))
    }

    fun addModelBox() {
        val panel = NoteBlock.ModelBox(id = newNoteBlockId())
        insertAtCaret(panel)
        caret = NoteCaret(NoteStyleTarget.PanelTitle, panel.id, 0, 0)
        pendingPanelFocusId = panel.id
    }

    fun updateTextBlock(id: String, text: String, start: Int = text.length, end: Int = text.length) {
        caret = NoteCaret(NoteStyleTarget.Text, id, start, end)
        val current = allBlocks.filterIsInstance<NoteBlock.Text>().find { it.id == id } ?: return
        if (current.text == text) return
        mapText(id) { block ->
            block.copy(text = text, spans = adjustSpans(block.text, text, block.spans))
        }
    }

    fun setCheckboxChecked(id: String, checked: Boolean) {
        commit(updateBlock(id) { block ->
            if (block is NoteBlock.Checkbox && block.id == id) block.copy(checked = checked) else block
        })
    }

    fun updateCheckboxLabel(id: String, label: String, start: Int = label.length, end: Int = label.length) {
        caret = NoteCaret(NoteStyleTarget.Checkbox, id, start, end)
        val current = allBlocks.filterIsInstance<NoteBlock.Checkbox>().find { it.id == id } ?: return
        if (current.label == label) return
        mapCheckbox(id) { block ->
            block.copy(label = label, spans = adjustSpans(block.label, label, block.spans))
        }
    }

    fun updateModelTitle(id: String, title: String, start: Int = title.length, end: Int = title.length) {
        caret = NoteCaret(NoteStyleTarget.PanelTitle, id, start, end)
        val current = allBlocks.filterIsInstance<NoteBlock.ModelBox>().find { it.id == id } ?: return
        if (current.title == title) return
        mapPanel(id) { block ->
            block.copy(title = title, titleSpans = adjustSpans(block.title, title, block.titleSpans))
        }
    }

    fun updatePanelType(id: String, type: NotePanelType) {
        mapPanel(id) { it.copy(type = type) }
    }

    fun updatePanelBackground(id: String, background: String?) {
        mapPanel(id) { it.copy(backgroundColor = background?.takeIf { color -> color.isNotBlank() }) }
    }

    private fun updateTextHeading(id: String, heading: NoteHeading) {
        mapText(id) { it.copy(heading = heading) }
    }

    private fun updateCheckboxHeading(id: String, heading: NoteHeading) {
        mapCheckbox(id) { it.copy(heading = heading) }
    }

    private fun updatePanelHeading(id: String, heading: NoteHeading) {
        mapPanel(id) { it.copy(titleHeading = heading) }
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
            NoteStyleTarget.PanelTitle -> mapPanel(currentCaret.blockId) { block ->
                block.copy(titleSpans = clearInline(block.title.length, block.titleSpans, start, end))
            }
            NoteStyleTarget.None -> Unit
        }
    }

    /**
     * Inserts a block right after the block holding the caret, inside that panel when the
     * caret already sits in one, so panels can be created exactly where the user types.
     */
    private fun insertAtCaret(block: NoteBlock, keepTrailingText: Boolean = false) {
        val panelId = focusedPanelId()
        if (panelId != null) {
            val panel = allBlocks.filterIsInstance<NoteBlock.ModelBox>().find { it.id == panelId } ?: return
            val at = panel.blocks.indexOfFirst { it.id == caret.blockId }
            val insertAt = if (at >= 0) at + 1 else panel.blocks.size
            val nextBlocks = panel.blocks.toMutableList().apply { add(insertAt, block) }
            mapPanel(panelId) { panelBlock ->
                panelBlock.copy(blocks = nextBlocks.withTrailingText(keepTrailingText))
            }
        } else {
            val at = blocks.indexOfFirst { it.id == caret.blockId }
            val insertAt = if (at >= 0) at + 1 else blocks.size
            val nextBlocks = blocks.toMutableList().apply { add(insertAt, block) }
            commit(nextBlocks.withTrailingText(keepTrailingText))
        }
    }

    private fun List<NoteBlock>.withTrailingText(enabled: Boolean): List<NoteBlock> {
        if (!enabled) return this
        val last = lastOrNull()
        return if (last == null || last is NoteBlock.Text) {
            this
        } else {
            this + NoteBlock.Text(newNoteBlockId(), "")
        }
    }

    private fun NoteBlock.toPlainText(): NoteBlock = when (this) {
        is NoteBlock.Checkbox -> NoteBlock.Text(id, label, heading, spans)
        is NoteBlock.ModelBox -> copy(blocks = blocks.map { it.toPlainText() })
        else -> this
    }

    private fun NoteBlock.toCheckboxes(): List<NoteBlock> = when (this) {
        is NoteBlock.Text -> {
            val lines = text.lines().ifEmpty { listOf("") }
            var offset = 0
            lines.map { line ->
                val lineSpans = spans.mapNotNull { span ->
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
                    heading = heading,
                    spans = lineSpans
                )
            }
        }

        is NoteBlock.ModelBox -> listOf(copy(blocks = blocks.flatMap { it.toCheckboxes() }))
        else -> listOf(this)
    }

    private fun mapText(id: String, transform: (NoteBlock.Text) -> NoteBlock.Text) {
        commit(updateBlock(id) { block ->
            if (block is NoteBlock.Text) transform(block) else block
        })
    }

    private fun mapCheckbox(id: String, transform: (NoteBlock.Checkbox) -> NoteBlock.Checkbox) {
        commit(updateBlock(id) { block ->
            if (block is NoteBlock.Checkbox) transform(block) else block
        })
    }

    private fun mapPanel(id: String, transform: (NoteBlock.ModelBox) -> NoteBlock.ModelBox) {
        commit(updateBlock(id) { block ->
            if (block is NoteBlock.ModelBox) transform(block) else block
        })
    }

    /** Rewrites the matching block wherever it lives, top level or nested in a panel. */
    private fun updateBlock(id: String, transform: (NoteBlock) -> NoteBlock): List<NoteBlock> =
        blocks.map { block -> block.updateWithin(id, transform) }

    private fun NoteBlock.updateWithin(id: String, transform: (NoteBlock) -> NoteBlock): NoteBlock {
        if (this.id == id) return transform(this)
        if (this is NoteBlock.ModelBox && containsBlock(id)) {
            return copy(blocks = blocks.map { child -> child.updateWithin(id, transform) })
        }
        return this
    }

    private fun commit(next: List<NoteBlock>) {
        blocks = next
        note = note?.copy(content = saveNoteContent(next, titleSpans))
    }
}