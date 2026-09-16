package com.example.personal_management_app.repositories

import androidx.compose.ui.graphics.Color

data class TrashItem(
    val id: String,
    val title: String,
    val content: String,
    val timeDeleted: String,
    val backgroundColor: Color = Color.White
)

class TrashRepository {
    fun getTrashItems(): List<TrashItem> {
        return listOf(
            TrashItem(
                id = "1",
                title = "Ý tưởng dự án cũ",
                content = "Xóa các module không cần thiết trong Jetpack Compose.",
                timeDeleted = "Đã xóa 2 ngày trước",
                backgroundColor = Color(0xFFFCE4EC)
            ),
            TrashItem(
                id = "2",
                title = "Ghi chú mua sắm",
                content = "Sữa, bánh mì, cà phê đen.",
                timeDeleted = "Đã xóa 5 ngày trước",
                backgroundColor = Color(0xFFE8F5E9)
            )
        )
    }
}