package com.example.personal_management_app.ui.screen.reminder

import android.content.Context
import com.example.personal_management_app.entities.NoteEntity
import com.example.personal_management_app.entities.NoteReminder
import com.example.personal_management_app.entities.ReminderRepeat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    private val alarmManager: ReminderAlarmManager,
    @ApplicationContext private val context: Context
) {
    fun sync(notes: List<NoteEntity>) {
        notes.forEach(::apply)
    }

    fun apply(note: NoteEntity) {
        val reminder = note.reminder
        if (reminder == null || note.archivedAt != null || note.deletedAt != null) {
            alarmManager.cancelAlarm(context, note.id)
            return
        }
        val trigger = nextTrigger(reminder) ?: run {
            alarmManager.cancelAlarm(context, note.id)
            return
        }
        alarmManager.schedule(
            context,
            note.id,
            note.title.ifBlank { "Nhắc nhở" },
            note.content,
            trigger
        )
    }

    fun cancel(noteId: String) {
        alarmManager.cancelAlarm(context, noteId)
    }

    fun nextAfterFired(reminder: NoteReminder): Long? {
        if (reminder.repeat == ReminderRepeat.Once) return null
        return advance(reminder.atMillis, reminder.repeat, System.currentTimeMillis())
    }

    private fun nextTrigger(reminder: NoteReminder): Long? {
        val now = System.currentTimeMillis()
        if (reminder.atMillis > now) return reminder.atMillis
        if (reminder.repeat == ReminderRepeat.Once) return null
        return advance(reminder.atMillis, reminder.repeat, now)
    }

    private fun advance(atMillis: Long, repeat: ReminderRepeat, now: Long): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = atMillis }
        do {
            when (repeat) {
                ReminderRepeat.Daily -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                ReminderRepeat.Weekly -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                ReminderRepeat.Monthly -> calendar.add(Calendar.MONTH, 1)
                ReminderRepeat.Once -> return calendar.timeInMillis
            }
        } while (calendar.timeInMillis <= now)
        return calendar.timeInMillis
    }
}
