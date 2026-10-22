package com.example.personal_management_app.ui.screen.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.personal_management_app.MainActivity
import com.example.personal_management_app.R
import com.example.personal_management_app.repositories.NoteRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var noteRepository: NoteRepository
    @Inject lateinit var scheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(ReminderAlarmManager.EXTRA_TITLE) ?: "Reminder"
        val description = intent.getStringExtra(ReminderAlarmManager.EXTRA_DESCRIPTION) ?: ""
        val reminderId = intent.getStringExtra(ReminderAlarmManager.EXTRA_ID) ?: ""

        showNotification(context, title, description, reminderId)
        reschedule(reminderId)
    }

    private fun reschedule(reminderId: String) {
        val note = noteRepository.get(reminderId) ?: return
        val reminder = note.reminder ?: return
        val next = scheduler.nextAfterFired(reminder) ?: return
        scheduler.apply(note.copy(reminder = reminder.copy(atMillis = next)))
    }

    private fun showNotification(context: Context, title: String, description: String, id: String) {
        createNotificationChannel(context)

        // Android 13+ (API 33) requires the runtime POST_NOTIFICATIONS permission.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val contentIntent = PendingIntent.getActivity(
            context,
            id.hashCode(),
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context, ReminderAlarmManager.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(context).notify(id.hashCode(), notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderAlarmManager.CHANNEL_ID,
                ReminderAlarmManager.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for note reminders"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
