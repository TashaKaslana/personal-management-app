package com.example.personal_management_app.entities

import kotlinx.serialization.Serializable

@Serializable
enum class ReminderRepeat {
    Once,
    Daily,
    Weekly,
    Monthly
}

@Serializable
data class NoteReminder(
    val atMillis: Long,
    val repeat: ReminderRepeat = ReminderRepeat.Once
)
