package com.example.personal_management_app.ui.screen.note_screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
sealed class NoteBlock {
    abstract val id: String

    @Serializable
    @SerialName("Text")
    data class Text(
        override val id: String,
        val text: String,
        val heading: NoteHeading = NoteHeading.Normal,
        val spans: List<NoteTextSpan> = emptyList()
    ) : NoteBlock()

    @Serializable
    @SerialName("Checkbox")
    data class Checkbox(
        override val id: String,
        val checked: Boolean,
        val label: String,
        val heading: NoteHeading = NoteHeading.Normal,
        val spans: List<NoteTextSpan> = emptyList()
    ) : NoteBlock()

    @Serializable
    @SerialName("Image")
    data class Image(
        override val id: String,
        val uri: String
    ) : NoteBlock()

    @Serializable
    @SerialName("ModelBox")
    data class ModelBox(
        override val id: String,
        val title: String,
        val body: String,
        val titleHeading: NoteHeading = NoteHeading.Normal,
        val titleSpans: List<NoteTextSpan> = emptyList(),
        val bodyHeading: NoteHeading = NoteHeading.Normal,
        val bodySpans: List<NoteTextSpan> = emptyList()
    ) : NoteBlock()
}

fun newNoteBlockId(): String = UUID.randomUUID().toString().take(8)

@Serializable
data class NoteContentDocument(
    val version: Int,
    val titleSpans: List<NoteTextSpan> = emptyList(),
    val blocks: List<NoteBlock> = emptyList()
)

private val noteContentJson = Json {
    ignoreUnknownKeys = true
}

fun saveNoteContent(
    blocks: List<NoteBlock>,
    titleSpans: List<NoteTextSpan> = emptyList()
): String {
    val document = NoteContentDocument(
        version = 1,
        titleSpans = titleSpans,
        blocks = blocks
    )
    return noteContentJson.encodeToString(NoteContentDocument.serializer(), document)
}

fun loadTitleSpans(content: String): List<NoteTextSpan> {
    val document = runCatching {
        noteContentJson.decodeFromString(NoteContentDocument.serializer(), content)
    }.getOrNull()
    return document?.titleSpans ?: emptyList()
}

fun loadNoteContent(content: String): List<NoteBlock> {
    if (content.isBlank()) {
        return listOf(NoteBlock.Text(newNoteBlockId(), ""))
    }
    val document = runCatching {
        noteContentJson.decodeFromString(NoteContentDocument.serializer(), content)
    }.getOrNull()
    return document?.blocks?.ifEmpty { null }
        ?: listOf(NoteBlock.Text(newNoteBlockId(), content))
}

@Composable
fun NoteContentPreview(
    content: String,
    modifier: Modifier = Modifier,
    maxBlocks: Int = 4,
    contentColor: Color = Color.DarkGray
) {
    val blocks = loadNoteContent(content)
    val selectedBlocks = blocks.take(maxBlocks)
    val textStyle = TextStyle(
        fontSize = 12.sp,
        color = contentColor
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        selectedBlocks.forEach { block ->
            when (block) {
                is NoteBlock.Text -> {
                    if (block.text.isNotBlank()) {
                        Text(
                            text = buildStyledText(block.text, block.spans),
                            style = textStyle.forPreview(block.heading),
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                is NoteBlock.Checkbox -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (block.checked) {
                                Icons.Filled.CheckBox
                            } else {
                                Icons.Filled.CheckBoxOutlineBlank
                            },
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = buildStyledText(block.label, block.spans),
                            style = textStyle.forPreview(block.heading),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                is NoteBlock.Image -> NoteImageBlock(
                    uri = block.uri,
                    maxHeight = 96.dp
                )

                is NoteBlock.ModelBox -> {
                    val preview = block.title.ifBlank { block.body }
                    val spans = if (block.title.isNotBlank()) block.titleSpans else block.bodySpans
                    val heading = if (block.title.isNotBlank()) block.titleHeading else block.bodyHeading
                    Text(
                        text = buildStyledText(preview, spans),
                        style = textStyle.copy(fontWeight = FontWeight.Medium).forPreview(heading),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (blocks.size > maxBlocks) {
            Text(text = "...", color = contentColor)
        }
    }
}

@Composable
fun NoteRenderer(
    blocks: List<NoteBlock>,
    textStyle: TextStyle,
    onTextChange: (String, String, Int, Int) -> Unit,
    onCheckboxChecked: (String, Boolean) -> Unit,
    onCheckboxLabelChange: (String, String, Int, Int) -> Unit,
    onModelBoxChange: (String, String, String, NoteStyleTarget, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        blocks.forEach { block ->
            when (block) {
                is NoteBlock.Text -> {
                    NoteStyledField(
                        text = block.text,
                        spans = block.spans,
                        style = textStyle.withHeading(block.heading),
                        onEdit = { text, start, end ->
                            onTextChange(block.id, text, start, end)
                        },
                        fieldKey = block.id,
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
                        NoteStyledField(
                            text = block.label,
                            spans = block.spans,
                            style = textStyle.withHeading(block.heading),
                            onEdit = { label, start, end ->
                                onCheckboxLabelChange(block.id, label, start, end)
                            },
                            fieldKey = block.id,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is NoteBlock.Image -> NoteImageBlock(uri = block.uri)

                is NoteBlock.ModelBox -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.55f)
                        )
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            NoteStyledField(
                                text = block.title,
                                spans = block.titleSpans,
                                style = textStyle.copy(fontWeight = FontWeight.Medium)
                                    .withHeading(block.titleHeading),
                                onEdit = { title, start, end ->
                                    onModelBoxChange(
                                        block.id,
                                        title,
                                        block.body,
                                        NoteStyleTarget.ModelTitle,
                                        start,
                                        end
                                    )
                                },
                                fieldKey = block.id + ":title",
                                modifier = Modifier.fillMaxWidth()
                            )
                            NoteStyledField(
                                text = block.body,
                                spans = block.bodySpans,
                                style = textStyle.withHeading(block.bodyHeading),
                                onEdit = { body, start, end ->
                                    onModelBoxChange(
                                        block.id,
                                        block.title,
                                        body,
                                        NoteStyleTarget.ModelBody,
                                        start,
                                        end
                                    )
                                },
                                fieldKey = block.id + ":body",
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
private fun NoteImageBlock(
    uri: String,
    maxHeight: Dp = 240.dp
) {
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
                .heightIn(max = maxHeight)
        )
    } else {
        Text(
            text = uri.ifBlank { "Image" },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
