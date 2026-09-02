package com.example.personal_management_app.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier,
    ) {
        Button(
            onClick = { navController.navigate("note_screen")}
        ) { Text("Note Screen") }

        Button(
            onClick = { navController.navigate("login_screen")}
        ) { Text("Login") }

        Button(
            onClick = { navController.navigate("register_screen")}
        ) { Text("Register") }
    }
}