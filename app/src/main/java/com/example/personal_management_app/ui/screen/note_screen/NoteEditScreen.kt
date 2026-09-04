package com.example.personal_management_app.ui.screen.note_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.personal_management_app.ui.components.NoteCard
import com.example.personal_management_app.viewmodel.NoteViewModel

@Composable
fun NoteEditScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    noteId: String,
    noteViewModel: NoteViewModel = viewModel()
) {
    val note = noteViewModel.getNote(noteId)

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NoteCard(
            title = note?.title ?: "Untitled",
            content = note?.content ?: "",
            modifier = modifier.padding(innerPadding),
            navController = navController
        )
    }
}