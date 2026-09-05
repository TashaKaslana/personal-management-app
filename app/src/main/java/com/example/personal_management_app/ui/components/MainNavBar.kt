package com.example.personal_management_app.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.personal_management_app.ui.theme.FabContainer
import com.example.personal_management_app.ui.theme.NavBarBackground

@Composable
fun MainNavBar(
    navController: NavController,
    currentTab: Int // Thêm tham số nhận diện tab hiện tại
) {
    NavigationBar(
        containerColor = NavBarBackground
    ) {
        // Tab 0: Ghi chú
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Edit, contentDescription = "Ghi chú") },
            label = { Text("Ghi chú") },
            selected = currentTab == 0, // Dựa vào currentTab để highlight
            onClick = {
                if (currentTab != 0) {
                    navController.navigate("note_screen") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        // Tab 1: Lời nhắc
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Lời nhắc") },
            label = { Text("Lời nhắc") },
            selected = currentTab == 1, // Dựa vào currentTab để highlight
            onClick = {
                if (currentTab != 1) {
                    navController.navigate("reminder_screen") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        // Tab 2: Lưu trữ
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Delete, contentDescription = "Lưu trữ") },
            label = { Text("Lưu trữ") },
            selected = currentTab == 2, // Dựa vào currentTab để highlight
            onClick = {
                if (currentTab != 2) {
                    navController.navigate("archive_screen") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}

@Composable
fun MainFloatingButton(navController: NavController) {
    FloatingActionButton(
        onClick = { navController.navigate("note_edit_screen/new") },
        containerColor = FabContainer,
        contentColor = Color.Black,
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm ghi chú")
    }
}