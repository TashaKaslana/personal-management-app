package com.example.personal_management_app.data.local.converters

import androidx.room.TypeConverter
import com.example.personal_management_app.dtos.NoteTextStyle
import com.example.personal_management_app.entities.NoteReminder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

class RoomConverters {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    @TypeConverter
    fun fromNoteTextStyle(style: NoteTextStyle?): String? {
        return style?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toNoteTextStyle(jsonString: String?): NoteTextStyle? {
        return jsonString?.let {
            runCatching { json.decodeFromString<NoteTextStyle>(it) }.getOrNull()
        } ?: NoteTextStyle()
    }

    @TypeConverter
    fun fromNoteReminder(reminder: NoteReminder?): String? {
        return reminder?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toNoteReminder(jsonString: String?): NoteReminder? {
        return jsonString?.let {
            runCatching { json.decodeFromString<NoteReminder>(it) }.getOrNull()
        }
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return json.encodeToString(list ?: emptyList())
    }

    @TypeConverter
    fun toStringList(jsonString: String?): List<String> {
        return jsonString?.let {
            runCatching { json.decodeFromString<List<String>>(it) }.getOrNull()
        } ?: emptyList()
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }
}
