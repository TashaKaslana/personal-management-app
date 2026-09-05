package com.example.personal_management_app.repositories

import com.example.personal_management_app.entites.ArchiveEntity
import com.example.personal_management_app.ui.theme.ArchiveBlue
import com.example.personal_management_app.ui.theme.ArchiveGreen
import com.example.personal_management_app.ui.theme.ArchivePurple

class ArchiveRepository {
    private val archives = mutableListOf(
        ArchiveEntity(
            id = "1",
            title = "Nhật ký du lịch Iceland",
            content = "Highlights: Skógafoss waterfall, Glacier lagoon walk. Need to backup photos.",
            tag = "Du lịch",
            timeOrStatusText = "Đã lưu trữ",
            backgroundColor = ArchiveBlue
        ),
        ArchiveEntity(
            id = "2",
            title = "Danh sách tìm nhà",
            content = "• Kiểm tra cách nhiệt\n• Kiểm tra áp lực nước\n• Lựa chọn tấm pin năng lượng",
            tag = null,
            timeOrStatusText = "Đã lưu trữ",
            backgroundColor = ArchiveGreen,
            isCheckList = true
        ),
        ArchiveEntity(
            id = "3",
            title = "Q3 Reflection Ghi chú",
            content = "Felt good about app development. Need more practice with Material 3 spacing dynamics.",
            tag = null,
            timeOrStatusText = "Đã lưu trữ",
            backgroundColor = ArchivePurple
        )
    )

    fun getArchives(): List<ArchiveEntity> {
        return archives
    }
}