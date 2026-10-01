package com.example.personal_management_app

import com.example.personal_management_app.ui.screen.note_screen.NoteBlock
import com.example.personal_management_app.ui.screen.note_screen.NoteHeading
import com.example.personal_management_app.ui.screen.note_screen.NoteTextSpan
import com.example.personal_management_app.ui.screen.note_screen.loadNoteContent
import com.example.personal_management_app.ui.screen.note_screen.loadTitleSpans
import com.example.personal_management_app.ui.screen.note_screen.saveNoteContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteContentFormatTest {

    @Test
    fun roundTripPreservesAllBlockTypes() {
        val blocks = listOf(
            NoteBlock.Text(
                id = "t1",
                text = "hello | pipe \n newline",
                heading = NoteHeading.H1,
                spans = listOf(NoteTextSpan(0, 5, bold = true))
            ),
            NoteBlock.Checkbox(
                id = "k1",
                checked = true,
                label = "done | thing",
                heading = NoteHeading.H2,
                spans = listOf(NoteTextSpan(0, 4, italic = true, underline = true))
            ),
            NoteBlock.Image(id = "i1", uri = "content://media/external/images/1"),
            NoteBlock.ModelBox(
                id = "m1",
                title = "box | title",
                body = "body \n text",
                titleHeading = NoteHeading.H1,
                titleSpans = listOf(NoteTextSpan(0, 3, underline = true)),
                bodySpans = listOf(NoteTextSpan(1, 2, bold = true))
            )
        )
        val titleSpans = listOf(NoteTextSpan(0, 3, bold = true, italic = true))

        val saved = saveNoteContent(blocks, titleSpans)

        assertEquals(blocks, loadNoteContent(saved))
        assertEquals(titleSpans, loadTitleSpans(saved))
    }

    @Test
    fun blankContentCreatesSingleEmptyTextBlock() {
        val blocks = loadNoteContent("")

        assertEquals(1, blocks.size)
        val block = blocks.first() as NoteBlock.Text
        assertEquals("", block.text)
    }

    @Test
    fun plainTextContentFallsBackToSingleTextBlock() {
        val raw = "Xây dựng app ghi chú với giao diện Material You."

        val blocks = loadNoteContent(raw)

        assertEquals(1, blocks.size)
        val block = blocks.first() as NoteBlock.Text
        assertEquals(raw, block.text)
    }

    @Test
    fun trailingEmptyTextBlockSurvivesRoundTrip() {
        val blocks = listOf(
            NoteBlock.Checkbox(id = "k1", checked = false, label = "task"),
            NoteBlock.Text(id = "t1", text = "")
        )

        assertEquals(blocks, loadNoteContent(saveNoteContent(blocks)))
    }

    @Test
    fun outputIsReadableJsonWithExplicitSpans() {
        val saved = saveNoteContent(
            listOf(NoteBlock.Text(id = "t1", text = "hi")),
            listOf(NoteTextSpan(0, 2, bold = true))
        )

        assertTrue(saved.contains("\"type\":\"Text\""))
        assertTrue(saved.contains("\"text\":\"hi\""))
        assertTrue(saved.contains("\"bold\":true"))
    }
}
