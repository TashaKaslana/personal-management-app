package com.example.personal_management_app.ui.screen.note_screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.personal_management_app.utils.toComposeColor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
enum class NotePanelType(val label: String) {
    Inherit("Inherit"),
    Plain("Box"),
    Callout("Callout"),
    Quote("Quote")
}

val notePanelTypes = listOf(
    NotePanelType.Inherit,
    NotePanelType.Plain,
    NotePanelType.Callout,
    NotePanelType.Quote
)

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
        val title: String = "",
        val titleHeading: NoteHeading = NoteHeading.Normal,
        val titleSpans: List<NoteTextSpan> = emptyList(),
        @SerialName("panelType")
        val type: NotePanelType = NotePanelType.Inherit,
        val backgroundColor: String? = null,
        val blocks: List<NoteBlock> = emptyList()
    ) : NoteBlock()
}

/** Blocks of a panel, depth first, so the panel itself is followed by its own children. */
fun NoteBlock.flattenBlocks(): List<NoteBlock> = when (this) {
    is NoteBlock.ModelBox -> listOf(this) + blocks.flatMap { it.flattenBlocks() }
    else -> listOf(this)
}

/** Searches this block and, when it is a panel, everything nested inside it. */
fun NoteBlock.findBlock(id: String): NoteBlock? {
    if (this.id == id) return this
    if (this is NoteBlock.ModelBox) {
        blocks.forEach { child ->
            val found = child.findBlock(id)
            if (found != null) return found
        }
    }
    return null
}

fun NoteBlock.containsBlock(id: String): Boolean = findBlock(id) != null

fun List<NoteBlock>.flattenBlocks(): List<NoteBlock> = flatMap { it.flattenBlocks() }

fun NoteBlock.ModelBox.effectiveBackground(parentBackground: String): String =
    backgroundColor ?: parentBackground

/** True when the panel draws no chrome and shows the parent background through it. */
val NoteBlock.ModelBox.isTransparent: Boolean
    get() = type == NotePanelType.Inherit && backgroundColor == null

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
        version = 2,
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
    val textStyle = TextStyle(
        fontSize = 12.sp,
        color = contentColor
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        NoteBlocksPreview(blocks, textStyle, maxBlocks, depth = 0)

        if (blocks.size > maxBlocks) {
            Text(text = "...", color = contentColor)
        }
    }
}

@Composable
private fun NoteBlocksPreview(
    blocks: List<NoteBlock>,
    textStyle: TextStyle,
    maxBlocks: Int,
    depth: Int
) {
    blocks.take(maxBlocks).forEach { block ->
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
                        tint = textStyle.color,
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

            is NoteBlock.ModelBox -> NotePanelPreview(block, textStyle, depth)
        }
    }
}

@Composable
private fun NotePanelPreview(
    block: NoteBlock.ModelBox,
    textStyle: TextStyle,
    depth: Int
) {
    val transparent = block.isTransparent
    val containerColor = when {
        transparent -> Color.Transparent
        block.backgroundColor != null -> block.backgroundColor.toComposeColor()
        else -> Color.White.copy(alpha = 0.5f)
    }
    val innerColor = block.backgroundColor?.let { noteContentColor(it) } ?: textStyle.color
    val innerStyle = textStyle.copy(
        color = innerColor,
        fontStyle = if (block.type == NotePanelType.Quote) FontStyle.Italic else textStyle.fontStyle
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor)
            .then(
                if (transparent) {
                    Modifier
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = Color.Gray.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            )
            .padding(if (transparent) 0.dp else 8.dp)
    ) {
        if (block.title.isNotBlank()) {
            Text(
                text = buildStyledText(block.title, block.titleSpans),
                style = innerStyle.copy(fontWeight = FontWeight.Medium)
                    .forPreview(block.titleHeading),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (depth < 1) {
            NoteBlocksPreview(block.blocks, innerStyle, maxBlocks = 2, depth = depth + 1)
        }
    }
}

/**
 * Draws one level of blocks. A panel calls this again with its own children.
 * Callbacks stay the same at every depth; the ViewModel finds the block by id.
 * [parentBackground] is what a panel inherits when its own background is null.
 */
@Composable
fun NoteRenderer(
    blocks: List<NoteBlock>,
    textStyle: TextStyle,
    parentBackground: String,
    onTextChange: (String, String, Int, Int) -> Unit,
    onCheckboxChecked: (String, Boolean) -> Unit,
    onCheckboxLabelChange: (String, String, Int, Int) -> Unit,
    onModelTitleChange: (String, String, Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    pendingPanelFocusId: String? = null,
    onPanelFocusConsumed: () -> Unit = {}
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
                    NotePanel(
                        block = block,
                        textStyle = textStyle,
                        parentBackground = parentBackground,
                        autoFocusTitle = pendingPanelFocusId == block.id,
                        onTitleFocusConsumed = onPanelFocusConsumed,
                        onTitleEdit = { title, start, end ->
                            onModelTitleChange(block.id, title, start, end)
                        }
                    ) { panelStyle ->
                        // Nested level: style and background come from this panel, not the note.
                        NoteRenderer(
                            blocks = block.blocks,
                            textStyle = panelStyle,
                            parentBackground = block.effectiveBackground(parentBackground),
                            onTextChange = onTextChange,
                            onCheckboxChecked = onCheckboxChecked,
                            onCheckboxLabelChange = onCheckboxLabelChange,
                            onModelTitleChange = onModelTitleChange,
                            pendingPanelFocusId = pendingPanelFocusId,
                            onPanelFocusConsumed = onPanelFocusConsumed
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotePanel(
    block: NoteBlock.ModelBox,
    textStyle: TextStyle,
    parentBackground: String,
    autoFocusTitle: Boolean,
    onTitleFocusConsumed: () -> Unit,
    onTitleEdit: (String, Int, Int) -> Unit,
    content: @Composable (TextStyle) -> Unit
) {
    val decorated = block.type != NotePanelType.Inherit
    val accentColor = when (block.type) {
        NotePanelType.Callout -> MaterialTheme.colorScheme.primary
        NotePanelType.Quote -> Color(0xFF9E9E9E)
        else -> null
    }
    val panelStyle = textStyle.copy(
        color = noteContentColor(block.effectiveBackground(parentBackground)),
        fontStyle = if (block.type == NotePanelType.Quote) FontStyle.Italic else textStyle.fontStyle
    )
    val focusRequester = remember(block.id) { FocusRequester() }

    LaunchedEffect(autoFocusTitle) {
        if (autoFocusTitle) {
            focusRequester.requestFocus()
            onTitleFocusConsumed()
        }
    }

    NoteThemedSurface(
        background = block.effectiveBackground(parentBackground),
        shape = RoundedCornerShape(if (decorated) 12.dp else 0.dp),
        transparent = block.isTransparent,
        border = when (block.type) {
            NotePanelType.Plain -> BorderStroke(1.dp, Color.Gray.copy(alpha = 0.25f))
            else -> null
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (accentColor != null) Modifier.height(IntrinsicSize.Min) else Modifier
                )
        ) {
            if (accentColor != null) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(accentColor)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(if (decorated) 10.dp else 0.dp)
            ) {
                NoteStyledField(
                    text = block.title,
                    spans = block.titleSpans,
                    style = panelStyle.copy(fontWeight = FontWeight.Medium)
                        .withHeading(block.titleHeading),
                    onEdit = onTitleEdit,
                    fieldKey = block.id + ":title",
                    focusRequester = focusRequester,
                    modifier = Modifier.fillMaxWidth()
                )
                content(panelStyle)
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