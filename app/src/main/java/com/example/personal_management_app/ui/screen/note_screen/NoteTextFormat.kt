package com.example.personal_management_app.ui.screen.note_screen

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

enum class NoteHeading(val fontSize: Float) {
    Normal(16f),
    H1(28f),
    H2(22f);

    fun encode(): String = when (this) {
        H1 -> "h1"
        H2 -> "h2"
        Normal -> "n"
    }
}

enum class NoteInlineStyle {
    Bold,
    Italic,
    Underline
}

enum class NoteStyleTarget {
    None,
    Title,
    Text,
    Checkbox,
    ModelTitle,
    ModelBody
}

data class NoteCaret(
    val target: NoteStyleTarget = NoteStyleTarget.None,
    val blockId: String = "",
    val start: Int = 0,
    val end: Int = 0
)

data class NoteTextSpan(
    val start: Int,
    val end: Int,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false
)

fun decodeHeading(raw: String?): NoteHeading = when (raw) {
    "h1" -> NoteHeading.H1
    "h2" -> NoteHeading.H2
    else -> NoteHeading.Normal
}

fun encodeSpans(spans: List<NoteTextSpan>): String {
    return spans.joinToString(",") { span ->
        "${span.start}-${span.end}-${span.encodeFlags()}"
    }
}

fun decodeSpans(raw: String): List<NoteTextSpan> {
    if (raw.isBlank()) return emptyList()
    return raw.split(',').mapNotNull { piece ->
        val parts = piece.split('-')
        val start = parts.getOrNull(0)?.toIntOrNull() ?: return@mapNotNull null
        val end = parts.getOrNull(1)?.toIntOrNull() ?: return@mapNotNull null
        val flags = parts.getOrNull(2).orEmpty()
        if (end <= start) return@mapNotNull null
        NoteTextSpan(
            start = start,
            end = end,
            bold = 'b' in flags,
            italic = 'i' in flags,
            underline = 'u' in flags
        )
    }
}

fun TextStyle.withHeading(heading: NoteHeading): TextStyle {
    return if (heading == NoteHeading.Normal) this else copy(fontSize = heading.fontSize.sp)
}

fun TextStyle.forPreview(heading: NoteHeading): TextStyle {
    val size = when (heading) {
        NoteHeading.H1 -> 16
        NoteHeading.H2 -> 14
        NoteHeading.Normal -> 12
    }
    return copy(fontSize = size.sp)
}

fun buildStyledText(
    text: String,
    spans: List<NoteTextSpan>
): AnnotatedString {
    if (text.isEmpty()) return AnnotatedString("")
    val flags = toFlagArray(text.length, spans)
    return buildAnnotatedString {
        var index = 0
        while (index < text.length) {
            val flag = flags[index]
            var end = index + 1
            while (end < text.length && flags[end] == flag) end++
            val chunk = text.substring(index, end)
            val spanStyle = flag.toSpanStyle()
            if (spanStyle == null) {
                append(chunk)
            } else {
                withStyle(spanStyle) { append(chunk) }
            }
            index = end
        }
    }
}

fun adjustSpans(oldText: String, newText: String, spans: List<NoteTextSpan>): List<NoteTextSpan> {
    if (oldText == newText || spans.isEmpty()) return spans
    val oldFlags = toFlagArray(oldText.length, spans)
    var prefix = 0
    val shared = min(oldText.length, newText.length)
    while (prefix < shared && oldText[prefix] == newText[prefix]) prefix++
    var suffix = 0
    while (
        suffix < shared - prefix &&
        oldText[oldText.length - 1 - suffix] == newText[newText.length - 1 - suffix]
    ) {
        suffix++
    }
    val oldEnd = oldText.length - suffix
    val newEnd = newText.length - suffix
    val inherited = if (oldEnd == prefix) {
        if (prefix > 0) oldFlags[prefix - 1] else StyleFlags()
    } else {
        oldFlags.getOrElse(prefix) { StyleFlags() }
    }
    val next = Array(newText.length) { StyleFlags() }
    for (index in 0 until prefix) next[index] = oldFlags[index]
    for (index in prefix until newEnd) next[index] = inherited
    for (index in 0 until suffix) {
        next[newText.length - 1 - index] = oldFlags[oldText.length - 1 - index]
    }
    return fromFlagArray(next)
}

fun toggleInline(
    textLength: Int,
    spans: List<NoteTextSpan>,
    start: Int,
    end: Int,
    style: NoteInlineStyle
): List<NoteTextSpan> {
    val from = min(start, end)
    val to = max(start, end)
    if (from == to) return spans
    val enable = !selectionHasStyle(textLength, spans, from, to, style)
    return setInline(textLength, spans, from, to, style, enable)
}

fun clearInline(
    textLength: Int,
    spans: List<NoteTextSpan>,
    start: Int,
    end: Int
): List<NoteTextSpan> {
    val from = min(start, end)
    val to = max(start, end)
    if (from == to || textLength == 0) return spans
    val flags = toFlagArray(textLength, spans)
    for (index in from.coerceIn(0, textLength) until to.coerceIn(0, textLength)) {
        flags[index] = StyleFlags()
    }
    return fromFlagArray(flags)
}

fun selectionHasStyle(
    textLength: Int,
    spans: List<NoteTextSpan>,
    start: Int,
    end: Int,
    style: NoteInlineStyle
): Boolean {
    val from = min(start, end).coerceIn(0, textLength)
    val to = max(start, end).coerceIn(0, textLength)
    if (from == to) return false
    val flags = toFlagArray(textLength, spans)
    return (from until to).all { flags[it].has(style) }
}

@Composable
fun NoteStyledField(
    text: String,
    spans: List<NoteTextSpan>,
    style: TextStyle,
    onEdit: (String, Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    fieldKey: Any = Unit
) {
    var fieldValue by remember(fieldKey) {
        mutableStateOf(TextFieldValue(text, androidx.compose.ui.text.TextRange(text.length)))
    }
    if (fieldValue.text != text) {
        fieldValue = TextFieldValue(text, androidx.compose.ui.text.TextRange(text.length))
    }

    BasicTextField(
        value = fieldValue.copy(annotatedString = buildStyledText(text, spans)),
        onValueChange = { next ->
            fieldValue = next
            onEdit(next.text, next.selection.min, next.selection.max)
        },
        textStyle = style,
        modifier = modifier.onFocusChanged { state ->
            if (state.isFocused) {
                onEdit(text, fieldValue.selection.min, fieldValue.selection.max)
            }
        }
    )
}

private data class StyleFlags(
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false
) {
    fun has(style: NoteInlineStyle): Boolean = when (style) {
        NoteInlineStyle.Bold -> bold
        NoteInlineStyle.Italic -> italic
        NoteInlineStyle.Underline -> underline
    }

    fun with(style: NoteInlineStyle, enabled: Boolean): StyleFlags = when (style) {
        NoteInlineStyle.Bold -> copy(bold = enabled)
        NoteInlineStyle.Italic -> copy(italic = enabled)
        NoteInlineStyle.Underline -> copy(underline = enabled)
    }

    fun toSpanStyle(): SpanStyle? {
        if (!bold && !italic && !underline) return null
        return SpanStyle(
            fontWeight = if (bold) FontWeight.Bold else null,
            fontStyle = if (italic) FontStyle.Italic else null,
            textDecoration = if (underline) TextDecoration.Underline else null
        )
    }
}

private fun NoteTextSpan.encodeFlags(): String = buildString {
    if (bold) append('b')
    if (italic) append('i')
    if (underline) append('u')
}

private fun toFlagArray(length: Int, spans: List<NoteTextSpan>): Array<StyleFlags> {
    val flags = Array(length) { StyleFlags() }
    spans.forEach { span ->
        val start = span.start.coerceIn(0, length)
        val end = span.end.coerceIn(0, length)
        for (index in start until end) {
            flags[index] = StyleFlags(
                bold = flags[index].bold || span.bold,
                italic = flags[index].italic || span.italic,
                underline = flags[index].underline || span.underline
            )
        }
    }
    return flags
}

private fun fromFlagArray(flags: Array<StyleFlags>): List<NoteTextSpan> {
    if (flags.isEmpty()) return emptyList()
    val spans = mutableListOf<NoteTextSpan>()
    var index = 0
    while (index < flags.size) {
        val flag = flags[index]
        var end = index + 1
        while (end < flags.size && flags[end] == flag) end++
        if (flag.bold || flag.italic || flag.underline) {
            spans += NoteTextSpan(
                start = index,
                end = end,
                bold = flag.bold,
                italic = flag.italic,
                underline = flag.underline
            )
        }
        index = end
    }
    return spans
}

private fun setInline(
    textLength: Int,
    spans: List<NoteTextSpan>,
    start: Int,
    end: Int,
    style: NoteInlineStyle,
    enabled: Boolean
): List<NoteTextSpan> {
    if (textLength == 0) return spans
    val flags = toFlagArray(textLength, spans)
    val from = start.coerceIn(0, textLength)
    val to = end.coerceIn(0, textLength)
    for (index in from until to) {
        flags[index] = flags[index].with(style, enabled)
    }
    return fromFlagArray(flags)
}
