package com.example.personal_management_app.ui.screen.note_screen

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.personal_management_app.utils.copyUriToNoteImageStorage
import com.example.personal_management_app.utils.toComposeColor

const val NOTE_IMAGE_THEME_PREFIX = "image:"

val noteColorThemes = listOf(
    "0xFFFFF8D6",
    "0xFFFFFFFF",
    "0xFFE2F6ED",
    "0xFFE3F2FD",
    "0xFFF1F8E9",
    "0xFFFCE4EC",
    "0xFFF3E5F5",
    "0xFFFFE0B2",
    "0xFFFFCCBC",
    "0xFFB2EBF2",
    "0xFFE1BEE7",
    "0xFFFFF3E0"
)

fun encodeNoteImageTheme(uri: String): String {
    val trimmed = uri.trim()
    return if (trimmed.startsWith(NOTE_IMAGE_THEME_PREFIX)) trimmed else "$NOTE_IMAGE_THEME_PREFIX$trimmed"
}

fun String.toNoteImageUri(): String? {
    if (!startsWith(NOTE_IMAGE_THEME_PREFIX)) return null
    return removePrefix(NOTE_IMAGE_THEME_PREFIX).takeIf { it.isNotBlank() }
}

fun noteContentColor(background: String): Color {
    if (background.toNoteImageUri() != null) return Color.Black
    val color = background.toComposeColor()
    val luminance = color.red * 0.299f + color.green * 0.587f + color.blue * 0.114f
    return if (luminance < 0.5f) Color.White else Color.Black
}

@Composable
fun NoteThemedSurface(
    background: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    expand: Boolean = false,
    transparent: Boolean = false,
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val imageUri = background.toNoteImageUri()
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = when {
                transparent -> Color.Transparent
                imageUri != null -> Color.White
                else -> background.toComposeColor()
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = border,
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        )
    ) {
        Box(modifier = if (expand) Modifier.fillMaxSize() else Modifier.fillMaxWidth()) {
            if (imageUri != null && !transparent) {
                NoteThemeImage(
                    uri = imageUri,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.White.copy(alpha = 0.45f))
                )
            }
            content()
        }
    }
}

@Composable
fun NoteThemeDialog(
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    onInherit: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val inheriting = onInherit != null && selected.isBlank()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val storedPath = copyUriToNoteImageStorage(context, uri)
            if (storedPath != null) {
                onSelect(encodeNoteImageTheme(storedPath))
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Theme") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (onInherit != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.clickable {
                            onInherit()
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = if (inheriting) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            }
                        )
                        Text(text = "Inherit from note")
                    }
                }
                noteColorThemes.chunked(6).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { hex ->
                            val selectedColor = selected == hex
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(hex.toComposeColor())
                                    .border(
                                        width = if (selectedColor) 2.dp else 1.dp,
                                        color = if (selectedColor) Color.Black else Color.LightGray,
                                        shape = CircleShape
                                    )
                                    .clickable { onSelect(hex) }
                            )
                        }
                    }
                }
                TextButton(onClick = {
                    imagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text("Custom image")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun NoteThemeImage(
    uri: String,
    modifier: Modifier = Modifier
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
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}
