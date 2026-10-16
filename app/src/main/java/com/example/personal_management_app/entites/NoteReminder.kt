package com.example.personal_management_app.entites

enum class ReminderRepeat {
    Once,
    Daily,
    Weekly,
    Monthly
}

data class NoteReminder(
    val atMillis: Long,
    val repeat: ReminderRepeat = ReminderRepeat.Once
)
