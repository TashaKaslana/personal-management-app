package com.example.personal_management_app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.personal_management_app.ui.screen.HomeScreen
import com.example.personal_management_app.ui.screen.LoginScreen
import com.example.personal_management_app.ui.screen.NoteScreen
import com.example.personal_management_app.ui.screen.RegisterScreen

@Composable
fun PersonalManagementApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            LoginScreen(navController = navController)
        }

        composable("login_screen") {
            LoginScreen(navController = navController)
        }

        composable("register_screen") {
            RegisterScreen(navController = navController)
        }

        composable("note_screen" ) {
            NoteScreen(navController = navController)
        }
    }
}