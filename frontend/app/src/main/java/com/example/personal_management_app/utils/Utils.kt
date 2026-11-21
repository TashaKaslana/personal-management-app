package com.example.personal_management_app.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.personal_management_app.entities.NoteEntity
import java.io.File
import java.text.Normalizer
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

/**
 * Copies an image returned by the photo picker into app internal storage for user avatar.
 * Cleans up previous avatars for the same user and returns the absolute file path.
 */
fun copyUriToAvatarStorage(context: Context, source: Uri, userId: String): String? {
    return runCatching {
        val dir = File(context.filesDir, "avatars").apply { mkdirs() }
        dir.listFiles()?.filter { it.name.startsWith("avatar_${userId}_") }?.forEach {
            it.delete()
        }
        val extension = context.contentResolver.getType(source)
            ?.substringAfterLast('/')
            ?.takeIf { it.isNotBlank() && it != "*" }
            ?: "jpg"
        val target = File(dir, "avatar_${userId}_${System.currentTimeMillis()}.$extension")
        context.contentResolver.openInputStream(source)?.use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Cannot open picked avatar image")
        target.absolutePath
    }.getOrNull()
}

/**
 * Deletes any stored avatar file for the given user.
 */
fun deleteAvatarFile(context: Context, userId: String) {
    runCatching {
        val dir = File(context.filesDir, "avatars")
        if (dir.exists()) {
            dir.listFiles()?.filter { it.name.startsWith("avatar_${userId}_") }?.forEach {
                it.delete()
            }
        }
    }
}

/**
 * Loads an ImageBitmap from a local file path or URI string safely.
 */
fun loadAvatarBitmap(context: Context, pathOrUri: String?): ImageBitmap? {
    if (pathOrUri.isNullOrBlank()) return null
    return runCatching {
        if (pathOrUri.startsWith("content://")) {
            val uri = Uri.parse(pathOrUri)
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        } else {
            val filePath = if (pathOrUri.startsWith("file://")) {
                Uri.parse(pathOrUri).path ?: pathOrUri.removePrefix("file://")
            } else {
                pathOrUri
            }
            val file = File(filePath)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
            } else {
                null
            }
        }
    }.getOrNull()
}

/**
 * Extracts initials from a user's display name (e.g., "Nguyễn Minh Đức" -> "NMD").
 */
fun getInitials(name: String): String {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return "U"
    val words = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }
    return if (words.size >= 2) {
        words.take(3).map { it.first().uppercaseChar() }.joinToString("")
    } else {
        trimmed.take(2).uppercase()
    }
}

/**
 * Removes Vietnamese accents/diacritics for accent-insensitive searching.
 */
fun String.removeAccents(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .replace('đ', 'd')
        .replace('Đ', 'D')
}

/**
 * Checks if a string contains the search query, with support for case-insensitive
 * and Vietnamese accent-insensitive matching.
 */
fun String.matchesSearch(query: String): Boolean {
    if (query.isBlank()) return true
    val cleanQuery = query.trim()
    if (this.contains(cleanQuery, ignoreCase = true)) return true
    val targetNorm = this.removeAccents()
    val queryNorm = cleanQuery.removeAccents()
    return targetNorm.contains(queryNorm, ignoreCase = true)
}

/**
 * Checks if a note matches the search query across title, content, and tags.
 */
fun NoteEntity.matchesQuery(query: String): Boolean {
    if (query.isBlank()) return true
    val tokens = query.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    val plainContent = com.example.personal_management_app.ui.components.note.getNotePlainText(content)
    val fullText = "$title $plainContent ${tags.joinToString(" ")}"
    return tokens.all { token ->
        fullText.matchesSearch(token)
    }
}

/**
 * Automatically exports SQLite database file directly into BlueStacks SharedFolder
 * (which maps directly to Windows: C:\ProgramData\BlueStacks_nxt\Engine\UserData\SharedFolder\)
 * as well as external storage, allowing real-time viewing in DataGrip with ZERO manual scripts!
 */
fun exportDatabase(context: Context) {
    runCatching {
        val dbFile = context.getDatabasePath("personal_management_app.db")
        if (dbFile.exists() && dbFile.length() > 0) {
            // 1. BlueStacks Shared Folder -> Maps to Windows C:\ProgramData\BlueStacks_nxt\Engine\UserData\SharedFolder
            val bstSharedDir = File("/sdcard/windows/BstSharedFolder")
            if (bstSharedDir.exists() && bstSharedDir.canWrite()) {
                dbFile.copyTo(File(bstSharedDir, "personal_management_app.db"), overwrite = true)
            }

            // 2. Standard external storage
            val extDir = context.getExternalFilesDir(null)
            if (extDir != null) {
                extDir.mkdirs()
                dbFile.copyTo(File(extDir, "personal_management_app.db"), overwrite = true)
            }

            // 3. Download directory
            val downloadDir = File("/sdcard/Download")
            if (downloadDir.exists() && downloadDir.canWrite()) {
                dbFile.copyTo(File(downloadDir, "personal_management_app.db"), overwrite = true)
            }
        }
    }
}
