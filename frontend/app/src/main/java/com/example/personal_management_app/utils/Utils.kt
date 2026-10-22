package com.example.personal_management_app.utils

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import java.io.File
import java.util.UUID

fun String.toComposeColor(): Color {
    return try {
        Color(removePrefix("0x").toLong(16).toInt())
    } catch (_: NumberFormatException) {
        Color.White
    }
}

/**
 * Copies an image returned by the photo picker into internal storage, because the
 * content URI grant is temporary and would break when the note is reopened later.
 * Returns the absolute file path, or null when copying fails.
 */
fun copyUriToNoteImageStorage(context: Context, source: Uri): String? {
    return runCatching {
        val dir = File(context.filesDir, "note_images").apply { mkdirs() }
        val extension = context.contentResolver.getType(source)
            ?.substringAfterLast('/')
            ?.takeIf { it.isNotBlank() && it != "*" }
            ?: "jpg"
        val target = File(dir, "note_${UUID.randomUUID()}.$extension")
        context.contentResolver.openInputStream(source)?.use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Cannot open picked image")
        target.absolutePath
    }.getOrNull()
}