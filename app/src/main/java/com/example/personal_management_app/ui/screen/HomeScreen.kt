package com.example.personal_management_app.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.personal_management_app.ui.screen.note_screen.NoteScreen

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    NoteScreen(modifier, navController)
}