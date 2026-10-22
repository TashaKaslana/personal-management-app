package com.example.personal_management_app.ui.screen.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresPermission
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderAlarmManager @Inject constructor() {

    companion object {
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
        const val EXTRA_ID = "EXTRA_ID"

        const val CHANNEL_ID = "reminders_channel"
        const val CHANNEL_NAME = "Reminders"
    }

    /**
     * Schedules an alarm for the given reminder.
     *
     * On Android 12+ (API 31) exact alarms require the SCHEDULE_EXACT_ALARM
     * special access (declared in the manifest). If the user has not granted it,
     * we fall back to an inexact alarm so reminders still fire (possibly with
     * a small delay). On Android 10/11 the permission is implicitly granted.
     */
    fun schedule(
        context: Context,
        reminderId: String,
        title: String,
        description: String,
        triggerTimeMillis: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, reminderId, title, description)

        if (triggerTimeMillis <= System.currentTimeMillis()) return

        if (canScheduleExactAlarms(context)) {
            setExact(alarmManager, triggerTimeMillis, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        }
    }

    fun cancelAlarm(context: Context, reminderId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, reminderId, "", "")
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    /**
     * Whether the app can schedule exact alarms. Always true below Android 12.
     */
    fun canScheduleExactAlarms(context: Context): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
    }

    /**
     * Intent that opens the system "Alarms & reminders" special access screen,
     * so the UI can prompt the user to grant exact alarm permission (Android 12+).
     */
    fun exactAlarmSettingsIntent(): Intent? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        } else null

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private fun setExact(
        alarmManager: AlarmManager,
        triggerTimeMillis: Long,
        pendingIntent: PendingIntent
    ) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )
    }

    private fun buildPendingIntent(
        context: Context,
        reminderId: String,
        title: String,
        description: String
    ): PendingIntent {
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = "com.example.personal_management_app.action.TRIGGER_REMINDER"
            putExtra(EXTRA_ID, reminderId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_DESCRIPTION, description)
        }
        return PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(), // unique per reminder so multiple alarms can coexist
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
