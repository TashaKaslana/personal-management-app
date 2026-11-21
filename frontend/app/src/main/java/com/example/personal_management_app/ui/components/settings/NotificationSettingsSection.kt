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
import com.example.personal_management_app.entities.DayPeriod
import com.example.personal_management_app.entities.NoteEntity
import com.example.personal_management_app.entities.SettingEntity
import com.example.personal_management_app.ui.components.reminder.label
import com.example.personal_management_app.ui.theme.Loginblue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsSection(
    settings: SettingEntity,
    onUpdateSettings: ((SettingEntity) -> SettingEntity) -> Unit,
    activeRemindersCount: Int,
    upcomingRemindersSummary: String,
    reminderNotes: List<NoteEntity>,
    modifier: Modifier = Modifier
) {
    var editingPeriod by remember { mutableStateOf<DayPeriod?>(null) }

    Column(modifier = modifier) {
        SettingsSectionHeader(title = "Tắt thông báo")

        SettingsSwitchItem(
            title = "Tắt tất cả thông báo",
            subtitle = if (settings.disableAllNotifications) {
                "Đang tắt tất cả thông báo 24/7 (kể cả sáng, chiều, tối)"
            } else {
                "Áp dụng theo từng buổi (Sáng, Chiều, Tối) bên dưới"
            },
            checked = settings.disableAllNotifications,
            onCheckedChange = { disabled ->
                onUpdateSettings { it.copy(disableAllNotifications = disabled) }
            }
        )

        SettingsSwitchItem(
            title = "Tắt thông báo Buổi sáng (5h - 12h)",
            subtitle = if (settings.disableMorning) {
                "Đang tắt thông báo lúc sáng (${settings.morningTime})"
            } else {
                "Nhận thông báo buổi sáng (Mặc định: ${settings.morningTime})"
            },
            checked = settings.disableMorning,
            onCheckedChange = { disabled ->
                onUpdateSettings { it.copy(disableMorning = disabled) }
            },
            enabled = !settings.disableAllNotifications
        )
        SettingsClickableItem(
            title = "Giờ thông báo buổi sáng",
            subtitle = "${settings.morningTime} (Nhấn để chỉnh giờ)",
            onClick = { editingPeriod = DayPeriod.MORNING }
        )

        SettingsSwitchItem(
            title = "Tắt thông báo Buổi chiều (13h - 17h)",
            subtitle = if (settings.disableAfternoon) {
                "Đang tắt thông báo lúc chiều (${settings.afternoonTime})"
            } else {
                "Nhận thông báo buổi chiều (Mặc định: ${settings.afternoonTime})"
            },
            checked = settings.disableAfternoon,
            onCheckedChange = { disabled ->
                onUpdateSettings { it.copy(disableAfternoon = disabled) }
            },
            enabled = !settings.disableAllNotifications
        )
        SettingsClickableItem(
            title = "Giờ thông báo buổi chiều",
            subtitle = "${settings.afternoonTime} (Nhấn để chỉnh giờ)",
            onClick = { editingPeriod = DayPeriod.AFTERNOON }
        )

        SettingsSwitchItem(
            title = "Tắt thông báo Buổi tối (18h - 4h sáng)",
            subtitle = if (settings.disableNight) {
                "Đang tắt thông báo ban đêm (${settings.nightTime})"
            } else {
                "Nhận thông báo buổi tối (Mặc định: ${settings.nightTime})"
            },
            checked = settings.disableNight,
            onCheckedChange = { disabled ->
                onUpdateSettings { it.copy(disableNight = disabled) }
            },
            enabled = !settings.disableAllNotifications
        )
        SettingsClickableItem(
            title = "Giờ thông báo buổi tối",
            subtitle = "${settings.nightTime} (Nhấn để chỉnh giờ)",
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
                text = "Lời nhắc đang có",
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
                val isNoteMuted = settings.disableAllNotifications || when (notePeriod) {
                    DayPeriod.MORNING -> settings.disableMorning
                    DayPeriod.AFTERNOON -> settings.disableAfternoon
                    DayPeriod.NIGHT -> settings.disableNight
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
            DayPeriod.MORNING -> settings.morningTime
            DayPeriod.AFTERNOON -> settings.afternoonTime
            DayPeriod.NIGHT -> settings.nightTime
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
                        DayPeriod.MORNING -> onUpdateSettings { it.copy(morningTime = formatted) }
                        DayPeriod.AFTERNOON -> onUpdateSettings { it.copy(afternoonTime = formatted) }
                        DayPeriod.NIGHT -> onUpdateSettings { it.copy(nightTime = formatted) }
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
