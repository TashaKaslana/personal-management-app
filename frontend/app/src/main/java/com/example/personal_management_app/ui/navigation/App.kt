package com.example.personal_management_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.personal_management_app.ui.screen.auth.ForgotPasswordScreen

import com.example.personal_management_app.ui.screen.HomeScreen
import com.example.personal_management_app.ui.screen.auth.LoginScreen

import com.example.personal_management_app.ui.screen.note_screen.NoteScreen
import com.example.personal_management_app.ui.screen.auth.RegisterScreen
import com.example.personal_management_app.ui.screen.archive.ArchiveScreen
import com.example.personal_management_app.ui.screen.note_screen.NoteEditScreen
import com.example.personal_management_app.ui.screen.profile.ProfileScreen
import com.example.personal_management_app.ui.screen.reminder.ReminderScreen
import com.example.personal_management_app.ui.screen.settings.SettingsScreen
import com.example.personal_management_app.ui.screen.tag.TagScreen
import com.example.personal_management_app.ui.screen.trash.TrashScreen


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
        composable("archive_screen") {
            ArchiveScreen(navController = navController)
        }
        composable("profile_screen") {
            ProfileScreen(navController = navController)
        }
        composable("forgot_password_screen") {
            ForgotPasswordScreen(navController = navController)
        }
        composable("notes/{noteId}/edit") {
            NoteEditScreen(navController = navController)
        }
        composable ("notes/add") {
            NoteEditScreen(navController = navController)
        }
        composable("trash_screen") {
            TrashScreen(navController = navController)
        }
        composable("labels_screen") {
            TagScreen(navController = navController)
        }
        composable("settings_screen") {
            SettingsScreen(navController = navController)
        }
    }
}
