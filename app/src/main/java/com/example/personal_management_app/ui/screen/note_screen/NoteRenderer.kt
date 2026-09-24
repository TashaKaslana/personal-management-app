package com.example.personal_management_app.ui.screen.note_screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import java.util.UUID

private const val FORMAT_HEADER = "PMA1"

sealed class NoteBlock {
    abstract val id: String

    data class Text(
        override val id: String,
        val text: String
    ) : NoteBlock()

    data class Checkbox(
        override val id: String,
        val checked: Boolean,
        val label: String
    ) : NoteBlock()

    data class Image(
        override val id: String,
        val uri: String
    ) : NoteBlock()

    data class ModelBox(
        override val id: String,
        val title: String,
        val body: String
    ) : NoteBlock()
}

fun newNoteBlockId(): String = UUID.randomUUID().toString().take(8)

fun saveNoteContent(blocks: List<NoteBlock>): String {
    return buildString {
        append(FORMAT_HEADER)
        blocks.forEach { block ->
            append('\n')
            append(
                when (block) {
                    is NoteBlock.Text -> "T|${block.id}|${escape(block.text)}"
                    is NoteBlock.Checkbox ->
                        "K|${block.id}|${if (block.checked) 1 else 0}|${escape(block.label)}"
                    is NoteBlock.Image -> "I|${block.id}|${escape(block.uri)}"
                    is NoteBlock.ModelBox ->
                        "M|${block.id}|${escape(block.title)}|${escape(block.body)}"
                }
            )
        }
    }
}

fun loadNoteContent(content: String): List<NoteBlock> {
    if (content.isBlank()) {
        return listOf(NoteBlock.Text(newNoteBlockId(), ""))
    }
    if (content.lineSequence().firstOrNull() != FORMAT_HEADER) {
        return listOf(NoteBlock.Text(newNoteBlockId(), content))
    }

    val blocks = content.lineSequence()
        .drop(1)
        .mapNotNull(::decodeLine)
        .toList()

    return blocks.ifEmpty { listOf(NoteBlock.Text(newNoteBlockId(), "")) }
}

fun noteContentPreview(content: String): String {
    return loadNoteContent(content).joinToString("\n") { block ->
        when (block) {
            is NoteBlock.Text -> block.text
            is NoteBlock.Checkbox -> block.label
            is NoteBlock.Image -> "[image]"
            is NoteBlock.ModelBox ->
                listOf(block.title, block.body)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
        }
    }.trim()
}

@Composable
fun NoteRenderer(
    blocks: List<NoteBlock>,
    textStyle: TextStyle,
    onTextChange: (String, String) -> Unit,
    onCheckboxChecked: (String, Boolean) -> Unit,
    onCheckboxLabelChange: (String, String) -> Unit,
    onModelBoxChange: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        blocks.forEach { block ->
            when (block) {
                is NoteBlock.Text -> {
                    BasicTextField(
                        value = block.text,
                        onValueChange = { onTextChange(block.id, it) },
                        textStyle = textStyle,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is NoteBlock.Checkbox -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = block.checked,
                            onCheckedChange = { onCheckboxChecked(block.id, it) }
                        )
                        BasicTextField(
                            value = block.label,
                            onValueChange = { onCheckboxLabelChange(block.id, it) },
                            textStyle = textStyle,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is NoteBlock.Image -> NoteImageBlock(uri = block.uri)

                is NoteBlock.ModelBox -> {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            BasicTextField(
                                value = block.title,
                                onValueChange = {
                                    onModelBoxChange(block.id, it, block.body)
                                },
                                textStyle = textStyle.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.fillMaxWidth()
                            )
                            BasicTextField(
                                value = block.body,
                                onValueChange = {
                                    onModelBoxChange(block.id, block.title, it)
                                },
                                textStyle = textStyle,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteImageBlock(uri: String) {
    val context = LocalContext.current
    val bitmap = remember(uri) {
        runCatching {
            if (uri.startsWith("/")) {
                BitmapFactory.decodeFile(uri)
            } else {
                context.contentResolver.openInputStream(uri.toUri())?.use {
                    BitmapFactory.decodeStream(it)
                }
            }
        }.getOrNull()
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Note image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 240.dp)
        )
    } else {
        Text(
            text = uri.ifBlank { "Image" },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun decodeLine(line: String): NoteBlock? {
    if (line.isBlank()) return null
    val fields = splitFields(line)
    val type = fields.getOrNull(0) ?: return null
    val id = fields.getOrNull(1)?.ifBlank { null } ?: newNoteBlockId()

    return when (type) {
        "T" -> NoteBlock.Text(
            id = id,
            text = fields.getOrElse(2) { "" }
        )
        "K" -> NoteBlock.Checkbox(
            id = id,
            checked = fields.getOrNull(2) == "1",
            label = fields.getOrElse(3) { "" }
        )
        "I" -> NoteBlock.Image(
            id = id,
            uri = fields.getOrElse(2) { "" }
        )
        "M" -> NoteBlock.ModelBox(
            id = id,
            title = fields.getOrElse(2) { "" },
            body = fields.getOrElse(3) { "" }
        )
        else -> null
    }
}

private fun splitFields(line: String): List<String> {
    val fields = mutableListOf<String>()
    val current = StringBuilder()
    var index = 0

    while (index < line.length) {
        when (val char = line[index]) {
            '\\' if index + 1 < line.length -> {
                when (line[index + 1]) {
                    '\\' -> current.append('\\')
                    'p' -> current.append('|')
                    'n' -> current.append('\n')
                    else -> current.append(line[index + 1])
                }
                index += 2
            }
            '|' -> {
                fields.add(current.toString())
                current.clear()
                index++
            }
            else -> {
                current.append(char)
                index++
            }
        }
    }

    fields.add(current.toString())
    return fields
}

private fun escape(value: String): String {
    return value
        .replace("\\", "\\\\")
        .replace("|", "\\p")
        .replace("\n", "\\n")
        .replace("\r", "")
}
