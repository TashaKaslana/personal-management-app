package com.example.personal_management_app.ui.layouts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.personal_management_app.ui.components.MainFloatingButton

import com.example.personal_management_app.ui.components.MainNavBar
import com.example.personal_management_app.ui.components.MainTopBar

@Composable
fun MainLayout(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            MainTopBar()
        },
        bottomBar = {
            MainNavBar(navController)
        },
        floatingActionButton = {
            MainFloatingButton(navController)
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        content(innerPadding)
    }
}