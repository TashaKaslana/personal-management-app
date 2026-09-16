package com.example.personal_management_app.ui.layouts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.personal_management_app.ui.components.MainFloatingButton
import com.example.personal_management_app.ui.components.MainNavBar
import com.example.personal_management_app.ui.components.MainSidebar
import com.example.personal_management_app.ui.components.MainTopBar
import kotlinx.coroutines.launch

@Composable
fun MainLayout(
    navController: NavController,
    currentTab: Int = 0,
    topBar: @Composable (onMenuClick: () -> Unit) -> Unit = { onMenuClick ->
        MainTopBar(navController = navController, onMenuClick = onMenuClick)
    },
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: "home_screen"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MainSidebar(
                currentRoute = currentRoute,
                navController = navController,
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                topBar {
                    coroutineScope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    }
                }
            },
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
}