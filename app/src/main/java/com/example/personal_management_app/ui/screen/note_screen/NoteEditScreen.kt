package com.example.personal_management_app.ui.screen.note_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.personal_management_app.dtos.toCompose
import com.example.personal_management_app.ui.components.note.NoteCardActionsBottom
import com.example.personal_management_app.ui.components.note.NoteCardActionsTop
import com.example.personal_management_app.ui.components.note.NoteCardTextEditor
import com.example.personal_management_app.ui.components.utils.ShowToastMessage
import com.example.personal_management_app.viewmodel.NoteEditViewModel

@Composable
fun NoteEditScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: NoteEditViewModel = hiltViewModel()
) {
    val note = viewModel.note

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
                        viewModel.upsertNote()
                        navController.navigate("note_screen")
                    },
                    onPinClick = {
                        viewModel.pin()
                    },
                    onSetNotificationClick = {
                        viewModel.setNotification()
                    },
                    onSetArchived = {
                        viewModel.setArchived()
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

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
                    bodyContent = {
                        NoteRenderer(
                            blocks = viewModel.blocks,
                            textStyle = note.contentStyle.toCompose().copy(color = contentColor),
                            onTextChange = viewModel::updateTextBlock,
                            onCheckboxChecked = viewModel::setCheckboxChecked,
                            onCheckboxLabelChange = viewModel::updateCheckboxLabel,
                            onModelBoxChange = viewModel::updateModelBox
                        )
                    }
                )

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