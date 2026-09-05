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
    currentTab: Int = 0,
    topBar: @Composable () -> Unit = { MainTopBar() },
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = topBar,
        bottomBar = {
            MainNavBar(navController = navController, currentTab = currentTab)
        },
        floatingActionButton = {
            MainFloatingButton(navController)
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        content(innerPadding)
    }
}