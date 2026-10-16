package com.example.personal_management_app.ui.screen.note_screen

import com.example.personal_management_app.entites.NoteReminder
import com.example.personal_management_app.entites.ReminderRepeat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun NoteReminder.label(): String {
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(atMillis))
    return when (repeat) {
        ReminderRepeat.Once -> {
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(atMillis))
            "$date, $time"
        }
        ReminderRepeat.Daily -> "Hàng ngày, $time"
        ReminderRepeat.Weekly -> "${weekdayLabel(atMillis)}, $time"
        ReminderRepeat.Monthly -> "Ngày ${dayOfMonth(atMillis)}, $time"
    }
}

fun ReminderRepeat.label(): String = when (this) {
    ReminderRepeat.Once -> "Một lần"
    ReminderRepeat.Daily -> "Hàng ngày"
    ReminderRepeat.Weekly -> "Hàng tuần"
    ReminderRepeat.Monthly -> "Hàng tháng"
}

fun reminderAt(dateUtcMillis: Long, hour: Int, minute: Int): Long {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = dateUtcMillis
    }
    return Calendar.getInstance().apply {
        set(Calendar.YEAR, utc.get(Calendar.YEAR))
        set(Calendar.MONTH, utc.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, utc.get(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

fun startOfDayUtc(millis: Long): Long {
    val local = Calendar.getInstance().apply { timeInMillis = millis }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun dayOfMonth(millis: Long): Int {
    return Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.DAY_OF_MONTH)
}

private fun weekdayLabel(millis: Long): String {
    return when (Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Thứ 2"
        Calendar.TUESDAY -> "Thứ 3"
        Calendar.WEDNESDAY -> "Thứ 4"
        Calendar.THURSDAY -> "Thứ 5"
        Calendar.FRIDAY -> "Thứ 6"
        Calendar.SATURDAY -> "Thứ 7"
        else -> "Chủ nhật"
    }
}
