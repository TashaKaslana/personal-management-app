package com.example.personal_management_app.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personal_management_app.entities.NoteEntity
import com.example.personal_management_app.ui.components.reminder.label
import com.example.personal_management_app.ui.theme.Loginblue

enum class DayPeriod(val title: String, val hourRangeText: String) {
    MORNING("Buổi sáng", "05:00 - 12:59"),
    AFTERNOON("Buổi chiều", "13:00 - 17:59"),
    NIGHT("Buổi tối & Đêm", "18:00 - 04:59");

    companion object {
        fun fromHour(hour: Int): DayPeriod {
            return when (hour) {
                in 5..12 -> MORNING
                in 13..17 -> AFTERNOON
                else -> NIGHT
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsSection(
    disableAllNotifications: Boolean,
    onDisableAllNotificationsChange: (Boolean) -> Unit,

    disableMorning: Boolean,
    onDisableMorningChange: (Boolean) -> Unit,
    morningTime: String,
    onMorningTimeChange: (String) -> Unit,

    disableAfternoon: Boolean,
    onDisableAfternoonChange: (Boolean) -> Unit,
    afternoonTime: String,
    onAfternoonTimeChange: (String) -> Unit,

    disableNight: Boolean,
    onDisableNightChange: (Boolean) -> Unit,
    nightTime: String,
    onNightTimeChange: (String) -> Unit,

    activeRemindersCount: Int,
    upcomingRemindersSummary: String,
    reminderNotes: List<NoteEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    var editingPeriod by remember { mutableStateOf<DayPeriod?>(null) }

    Column(modifier = modifier) {
        SettingsSectionHeader(title = "Tắt thông báo")

        SettingsSwitchItem(
            title = "Tắt tất cả thông báo",
            subtitle = if (disableAllNotifications) {
                "Đang tắt tất cả thông báo 24/7 (kể cả sáng, chiều, tối)"
            } else {
                "Áp dụng theo từng buổi (Sáng, Chiều, Tối) bên dưới"
            },
            checked = disableAllNotifications,
            onCheckedChange = onDisableAllNotificationsChange
        )

        SettingsSwitchItem(
            title = "Tắt thông báo Buổi sáng (5h - 12h)",
            subtitle = if (disableMorning) "Đang tắt thông báo lúc sáng ($morningTime)" else "Nhận thông báo buổi sáng (Mặc định: $morningTime)",
            checked = disableMorning,
            onCheckedChange = onDisableMorningChange,
            enabled = !disableAllNotifications
        )
        SettingsClickableItem(
            title = "  └ Giờ thông báo buổi sáng",
            subtitle = "$morningTime (Nhấn để chỉnh giờ)",
            onClick = { editingPeriod = DayPeriod.MORNING }
        )

        SettingsSwitchItem(
            title = "Tắt thông báo Buổi chiều (13h - 17h)",
            subtitle = if (disableAfternoon) "Đang tắt thông báo lúc chiều ($afternoonTime)" else "Nhận thông báo buổi chiều (Mặc định: $afternoonTime)",
            checked = disableAfternoon,
            onCheckedChange = onDisableAfternoonChange,
            enabled = !disableAllNotifications
        )
        SettingsClickableItem(
            title = "  └ Giờ thông báo buổi chiều",
            subtitle = "$afternoonTime (Nhấn để chỉnh giờ)",
            onClick = { editingPeriod = DayPeriod.AFTERNOON }
        )

        SettingsSwitchItem(
            title = "Tắt thông báo Buổi tối (18h - 4h sáng)",
            subtitle = if (disableNight) "Đang tắt thông báo ban đêm ($nightTime)" else "Nhận thông báo buổi tối (Mặc định: $nightTime)",
            checked = disableNight,
            onCheckedChange = onDisableNightChange,
            enabled = !disableAllNotifications
        )
        SettingsClickableItem(
            title = "  └ Giờ thông báo buổi tối",
            subtitle = "$nightTime (Nhấn để chỉnh giờ)",
            onClick = { editingPeriod = DayPeriod.NIGHT }
        )

        SettingsClickableItem(
            title = "Tổng số lời nhắc hiện có",
            subtitle = if (activeRemindersCount > 0) {
                "$activeRemindersCount ghi chú đang có lời nhắc"
            } else {
                "Không có lời nhắc nào"
            },
            onClick = { }
        )

        SettingsClickableItem(
            title = "Lời nhắc sắp tới gần nhất",
            subtitle = upcomingRemindersSummary,
            onClick = { }
        )

        if (reminderNotes.isNotEmpty()) {
            Text(
                text = "DỮ LIỆU LỜI NHẮC TRÊN THỰC TẾ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            reminderNotes.forEach { note ->
                val timeLabel = note.reminder?.label() ?: "Chưa đặt giờ"
                val reminderHour = note.reminder?.let {
                    java.util.Calendar.getInstance().apply { timeInMillis = it.atMillis }.get(java.util.Calendar.HOUR_OF_DAY)
                } ?: 0
                val notePeriod = DayPeriod.fromHour(reminderHour)
                val isNoteMuted = disableAllNotifications || when (notePeriod) {
                    DayPeriod.MORNING -> disableMorning
                    DayPeriod.AFTERNOON -> disableAfternoon
                    DayPeriod.NIGHT -> disableNight
                }

                SettingsClickableItem(
                    title = note.title.ifBlank { "Không có tiêu đề" },
                    subtitle = if (isNoteMuted) {
                        "Đã tắt thông báo (${notePeriod.title}) • $timeLabel"
                    } else {
                        "Đang phát chuông (${notePeriod.title}) • $timeLabel"
                    },
                    onClick = { }
                )
            }
        }
    }

    editingPeriod?.let { period ->
        val initialTime = when (period) {
            DayPeriod.MORNING -> morningTime
            DayPeriod.AFTERNOON -> afternoonTime
            DayPeriod.NIGHT -> nightTime
        }
        val parts = initialTime.split(":")
        val initialHour = parts.getOrNull(0)?.toIntOrNull() ?: when (period) {
            DayPeriod.MORNING -> 8
            DayPeriod.AFTERNOON -> 13
            DayPeriod.NIGHT -> 18
        }
        val initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { editingPeriod = null },
            title = {
                Text(
                    text = "Chỉnh giờ cho ${period.title} (${period.hourRangeText})"
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val formatted = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    when (period) {
                        DayPeriod.MORNING -> onMorningTimeChange(formatted)
                        DayPeriod.AFTERNOON -> onAfternoonTimeChange(formatted)
                        DayPeriod.NIGHT -> onNightTimeChange(formatted)
                    }
                    editingPeriod = null
                }) {
                    Text("Lưu", color = Loginblue)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingPeriod = null }) {
                    Text("Hủy")
                }
            }
        )
    }
}
