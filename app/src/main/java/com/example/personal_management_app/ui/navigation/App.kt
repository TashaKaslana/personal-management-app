package com.example.personal_management_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.personal_management_app.ui.screen.HomeScreen
import com.example.personal_management_app.ui.screen.LoginScreen
import com.example.personal_management_app.ui.screen.note_screen.NoteScreen
import com.example.personal_management_app.ui.screen.RegisterScreen
import com.example.personal_management_app.ui.screen.note_screen.NoteEditScreen
import com.example.personal_management_app.ui.screen.reminder_screen.ReminderScreen


@Composable
fun PersonalManagementApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login_screen"
    ) {
        composable("login_screen") {
            LoginScreen(navController = navController)
        }
        composable("home_screen") {
            HomeScreen(navController = navController)
        }

        composable("register_screen") {
            RegisterScreen(navController = navController)
        }
        composable("note_screen") {
            NoteScreen(navController = navController)
        }
        composable("reminder_screen") {
            ReminderScreen(navController = navController)
        }

        composable("note_edit_screen/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""

            NoteEditScreen(navController = navController, noteId = noteId)
        }
    }
}