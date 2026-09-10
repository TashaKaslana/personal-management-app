package com.example.personal_management_app.ui.screen.note_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.personal_management_app.ui.components.note.NoteCardActionsBottom
import com.example.personal_management_app.ui.components.note.NoteCardActionsTop
import com.example.personal_management_app.ui.components.note.NoteCardTextEditor
import com.example.personal_management_app.ui.components.utils.ShowToastMessage
import com.example.personal_management_app.viewmodel.NoteEditViewModel

fun click() {
    print("Clicked!")
}

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
        Card(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp)
                .padding(innerPadding),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {
                NoteCardActionsTop(
                    onBackClick = {
                        viewModel.updateNote()
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

                NoteCardTextEditor(
                    modifier = Modifier.weight(1f),
                    note = note
                )

                NoteCardActionsBottom(
                    onAddClick = { click() },
                    onMenuClick = { click() },
                    onThemeClick = { click() },
                    onStyleClick = { click() }
                )
            }
        }
    }
}