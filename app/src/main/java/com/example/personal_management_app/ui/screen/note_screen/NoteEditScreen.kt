package com.example.personal_management_app.ui.screen.note_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.personal_management_app.dtos.toCompose
import com.example.personal_management_app.ui.components.TagChip
import com.example.personal_management_app.ui.components.note.NoteCardActionsBottom
import com.example.personal_management_app.ui.components.note.NoteCardActionsTop
import com.example.personal_management_app.ui.components.note.NoteCardTextEditor
import com.example.personal_management_app.ui.components.reminder.ReminderPickerDialog
import com.example.personal_management_app.ui.components.reminder.label
import com.example.personal_management_app.ui.components.utils.ShowToastMessage
import com.example.personal_management_app.viewmodel.NoteEditViewModel
import com.example.personal_management_app.viewmodel.TagViewModel

@Composable
fun NoteEditScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NoteEditViewModel = hiltViewModel(),
    tagViewModel: TagViewModel = hiltViewModel()
) {
    val note = viewModel.note
    val readOnly = note?.archivedAt != null || note?.deletedAt != null
    val tags by tagViewModel.tags.collectAsStateWithLifecycle()
    val tagColors = remember(tags) { tags.associate { it.name to it.color } }
    var showReminderDialog by remember { mutableStateOf(false) }

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
                    Text(
                        text = if (note.deletedAt != null) "Đã xóa" else "Đã lưu trữ",
                        style = MaterialTheme.typography.labelMedium,
                        color = contentColor
                    )
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
                }
            }
        }
    }
}