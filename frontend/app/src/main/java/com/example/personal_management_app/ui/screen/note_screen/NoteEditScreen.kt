package com.example.personal_management_app.ui.screen.note_screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.RestoreFromTrash
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.personal_management_app.dtos.toCompose
import com.example.personal_management_app.ui.components.tag.TagChip
import com.example.personal_management_app.ui.components.note.NoteCardActionsBottom
import com.example.personal_management_app.ui.components.note.NoteCardActionsTop
import com.example.personal_management_app.ui.components.note.NoteCardTextEditor
import com.example.personal_management_app.ui.components.note.NoteRenderer
import com.example.personal_management_app.ui.components.note.NoteThemedSurface
import com.example.personal_management_app.ui.components.note.noteContentColor
import com.example.personal_management_app.ui.components.reminder.ReminderPickerDialog
import com.example.personal_management_app.ui.components.reminder.label
import com.example.personal_management_app.ui.components.utils.ShowToastMessage
import com.example.personal_management_app.viewmodel.NoteEditViewModel
import com.example.personal_management_app.viewmodel.TagViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NoteEditScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NoteEditViewModel = hiltViewModel(),
    tagViewModel: TagViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val note = viewModel.note
    val readOnly = note?.archivedAt != null || note?.deletedAt != null
    val tags by tagViewModel.tags.collectAsStateWithLifecycle()
    val tagColors = remember(tags) { tags.associate { it.name to it.color } }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showDeleteForeverDialog by remember { mutableStateOf(false) }

    if (note == null) {
        ShowToastMessage(message = "Ghi chú không có sẵn, vui lòng thử lại")

        LaunchedEffect(Unit) {
            navController.navigate("note_screen")
        }
        return
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val contentColor = noteContentColor(note.backgroundColor)
        NoteThemedSurface(
            background = note.backgroundColor,
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp)
                .padding(innerPadding),
            shape = RoundedCornerShape(16.dp),
            expand = true
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {
                NoteCardActionsTop(
                    onBackClick = {
                        if (readOnly) {
                            if (!navController.popBackStack()) {
                                navController.navigate(
                                    if (note.deletedAt != null) "trash_screen" else "archive_screen"
                                )
                            }
                        } else {
                            viewModel.upsertNote()
                            if (!navController.popBackStack()) {
                                navController.navigate("note_screen")
                            }
                        }
                    },
                    onPinClick = {
                        viewModel.pin()
                    },
                    onSetNotificationClick = {
                        showReminderDialog = true
                    },
                    reminderSet = note.reminder != null,
                    onSetArchived = {
                        viewModel.setArchived()
                        navController.navigate("note_screen")
                    },
                    showActions = !readOnly
                )

                if (readOnly) {
                    if (note.deletedAt != null) {
                        val deletedTime = note.deletedAt ?: System.currentTimeMillis()
                        val dateFormatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(deletedTime))
                        val retentionMs = 30L * 24L * 60L * 60L * 1000L
                        val oneDayMs = 24L * 60L * 60L * 1000L
                        val diffMs = (deletedTime + retentionMs) - System.currentTimeMillis()
                        val daysLeft = if (diffMs <= 0L) 0 else ((diffMs + oneDayMs - 1) / oneDayMs).coerceIn(0L, 30L).toInt()
                        val remainingText = when {
                            daysLeft <= 0 -> "sắp bị xóa vĩnh viễn hôm nay"
                            daysLeft == 1 -> "còn 1 ngày nữa sẽ xóa"
                            else -> "còn $daysLeft ngày nữa sẽ xóa"
                        }
                        Text(
                            text = "Đã xóa: $dateFormatted • $remainingText",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (daysLeft <= 3) MaterialTheme.colorScheme.error else contentColor.copy(alpha = 0.8f)
                        )
                    } else {
                        Text(
                            text = "Đã lưu trữ",
                            style = MaterialTheme.typography.labelMedium,
                            color = contentColor
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                note.reminder?.let { reminder ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.clickable(enabled = !readOnly) {
                            showReminderDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = reminder.label(),
                            color = contentColor,
                            fontSize = 12.sp
                        )
                    }
                }

                if (note.tags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        maxLines = 2
                    ) {
                        note.tags.forEach { name ->
                            TagChip(
                                name = name,
                                color = tagColors[name]
                            )
                        }
                    }
                }

                NoteCardTextEditor(
                    modifier = Modifier.weight(1f),
                    note = note,
                    onTitleUpdate = { title, start, end ->
                        viewModel.updateTitle(title, start, end)
                    },
                    titleSpans = viewModel.titleSpans,
                    onContentUpdate = {
                        viewModel.updateContent(it)
                    },
                    contentColor = contentColor,
                    readOnly = readOnly,
                    bodyContent = {
                        NoteRenderer(
                            blocks = viewModel.blocks,
                            textStyle = note.contentStyle.toCompose().copy(color = contentColor),
                            parentBackground = note.backgroundColor,
                            onTextChange = viewModel::updateTextBlock,
                            onCheckboxChecked = viewModel::setCheckboxChecked,
                            onCheckboxLabelChange = viewModel::updateCheckboxLabel,
                            onModelTitleChange = viewModel::updateModelTitle,
                            pendingPanelFocusId = viewModel.pendingPanelFocusId,
                            focusedPanelId = viewModel.focusedPanelId(),
                            onPanelFocusConsumed = viewModel::consumePanelFocus,
                            readOnly = readOnly
                        )
                    }
                )

                if (showReminderDialog) {
                    ReminderPickerDialog(
                        initial = note.reminder,
                        onDismiss = { showReminderDialog = false },
                        onSave = {
                            viewModel.setReminder(it)
                            showReminderDialog = false
                        },
                        onClear = {
                            viewModel.clearReminder()
                            showReminderDialog = false
                        }
                    )
                }

                if (!readOnly) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    NoteCardActionsBottom(
                        viewModel = viewModel
                    )
                } else if (note.deletedAt != null) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.restore()
                                Toast.makeText(context, "Đã khôi phục ghi chú", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.RestoreFromTrash,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Khôi phục")
                        }

                        Button(
                            onClick = { showDeleteForeverDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteForever,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Xóa vĩnh viễn")
                        }
                    }
                }
            }
        }
    }

    if (showDeleteForeverDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteForeverDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(text = "Xóa vĩnh viễn ghi chú?")
            },
            text = {
                Text(text = "Ghi chú này sẽ bị xóa vĩnh viễn khỏi thiết bị và không thể khôi phục.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteForever()
                        showDeleteForeverDialog = false
                        Toast.makeText(context, "Đã xóa vĩnh viễn ghi chú", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                ) {
                    Text("Xóa vĩnh viễn", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteForeverDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}