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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainNavBar(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }

    NavigationBar(
        containerColor = Color(0xFFFCF8F2)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Edit, contentDescription = "Ghi chú") },
            label = { Text("Ghi chú") },
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Lời nhắc") },
            label = { Text("Lời nhắc") },
            selected = selectedTab == 1,
            onClick = {
                selectedTab = 1
                navController.navigate("reminder_screen") {
                    popUpTo("home_screen") { inclusive = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Delete, contentDescription = "Lưu trữ") },
            label = { Text("Lưu trữ") },
            selected = selectedTab == 2,
            onClick = {
                selectedTab = 2
                navController.navigate("archive_screen") {
                    popUpTo("home_screen") { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun MainFloatingButton(navController: NavController) {
    FloatingActionButton(
        onClick = { navController.navigate("note_edit_screen") },
        containerColor = Color(0xFFFFE082),
        contentColor = Color.Black,
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm ghi chú")
    }
}