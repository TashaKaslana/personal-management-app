package com.example.personal_management_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


data class NoteItem(
    val title: String,
    val content: String,
    val tag: String? = null,
    val backgroundColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Ghi chú, 1: Lời nhắc, 2: Lưu trữ
    var searchQuery by remember { mutableStateOf("") }


    val notesList = listOf(
        NoteItem("App Idea Sketch", "Xây dựng app ghi chú với giao diện Material You đẹp mắt.", "Công việc", Color(0xFFFFF8D6)),
        NoteItem("Danh sách tạp hóa", "• Táo hữu cơ\n• Almond Sữa\n• Sữa chua Hy Lạp", null, Color(0xFFE2F6ED)),
        NoteItem("Du lịch itinerary", "Chi tiết lộ trình và đặt phòng cho chuyến đi Iceland.", "Du lịch", Color(0xFFE3F2FD)),
        NoteItem("Quy tắc thiết kế M3", "Đảm bảo bo góc 12px cho thẻ và nút FAB.", null, Color(0xFFF1F8E9)),
        NoteItem("Nhắc nhở", "Call landlord to negotiate lease renew terms.\n🕒 Ngày mai, 10:00 AM", null, Color(0xFFFCE4EC)),
        NoteItem("Công việcout Goals", "• Giãn cơ 10 phút\n• 6k Morning Run\n• Tập thể lực", null, Color(0xFFF3E5F5))
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFFCF8F2),
        bottomBar = {
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("note_screen") },
                containerColor = Color(0xFFFFE082),
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm ghi chú")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tìm kiếm ghi chú", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.DarkGray) },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Grid/List", tint = Color.DarkGray)
                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFD7CCC8),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("NMD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ĐÃ GHIM",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalItemSpacing = 10.dp,
                modifier = Modifier.fillMaxSize()
            ) {
                items(notesList) { note ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = note.backgroundColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = note.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = note.content,
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                            if (note.tag != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = note.tag,
                                        fontSize = 10.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}