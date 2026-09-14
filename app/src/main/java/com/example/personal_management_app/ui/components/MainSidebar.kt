package com.example.personal_management_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.personal_management_app.ui.theme.SidebarSelectedColor
import com.example.personal_management_app.ui.theme.SidebarTextSelected
import com.example.personal_management_app.ui.theme.SidebarTextUnselected

data class SidebarItem(
    val title: String,
    val route: String
)

@Composable
fun MainSidebar(
    currentRoute: String,
    navController: NavController,
    onCloseDrawer: () -> Unit
) {
    val sidebarItems = listOf(
        SidebarItem("Ghi chú", "note_screen"),
        SidebarItem("Lời nhắc", "reminder_screen"),
        SidebarItem("Tạo nhãn mới", "labels_screen"),
        SidebarItem("Lưu trữ", "archive_screen"),
        SidebarItem("Thùng rác", "trash_screen"),
        SidebarItem("Cài đặt", "settings_screen"),
        SidebarItem("Trợ giúp và phản hồi", "help_screen")
    )

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Tiện ích",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SidebarTextUnselected,
                modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
            )

            sidebarItems.forEach { item ->
                val isSelected = currentRoute.startsWith(item.route)

                val backgroundColor = if (isSelected) SidebarSelectedColor.copy(alpha = 0.6f) else Color.Transparent
                val textColor = if (isSelected) SidebarTextSelected else SidebarTextUnselected

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 12.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                        .background(backgroundColor)
                        .clickable {
                            onCloseDrawer()
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo("note_screen") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                }
            }
        }
    }
}