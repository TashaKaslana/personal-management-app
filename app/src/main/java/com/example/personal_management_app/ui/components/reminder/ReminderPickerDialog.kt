package com.example.personal_management_app.ui.components.reminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.personal_management_app.entites.NoteReminder
import com.example.personal_management_app.entites.ReminderRepeat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderPickerDialog(
    initial: NoteReminder?,
    onDismiss: () -> Unit,
    onSave: (NoteReminder) -> Unit,
    onClear: () -> Unit
) {
    val seed = initial?.atMillis ?: Calendar.getInstance().apply {
        add(Calendar.HOUR_OF_DAY, 1)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val clock = Calendar.getInstance().apply { timeInMillis = seed }
    val dateState = rememberDatePickerState(initialSelectedDateMillis = startOfDayUtc(seed))
    val timeState = rememberTimePickerState(
        initialHour = clock.get(Calendar.HOUR_OF_DAY),
        initialMinute = clock.get(Calendar.MINUTE),
        is24Hour = true
    )
    var repeat by remember { mutableStateOf(initial?.repeat ?: ReminderRepeat.Once) }
    var pickingTime by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lời nhắc") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !pickingTime,
                        onClick = { pickingTime = false },
                        label = { Text("Ngày") }
                    )
                    FilterChip(
                        selected = pickingTime,
                        onClick = { pickingTime = true },
                        label = { Text("Giờ") }
                    )
                }
                if (pickingTime) {
                    TimePicker(state = timeState)
                } else {
                    DatePicker(
                        state = dateState,
                        title = null,
                        headline = null,
                        showModeToggle = false
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxLines = 2
                ) {
                    ReminderRepeat.entries.forEach { option ->
                        FilterChip(
                            selected = repeat == option,
                            onClick = { repeat = option },
                            label = { Text(option.label()) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val dateMillis = dateState.selectedDateMillis ?: return@TextButton
                onSave(
                    NoteReminder(
                        atMillis = reminderAt(dateMillis, timeState.hour, timeState.minute),
                        repeat = repeat
                    )
                )
            }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            Row {
                if (initial != null) {
                    TextButton(onClick = onClear) {
                        Text("Bỏ")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Hủy")
                }
            }
        }
    )
}
