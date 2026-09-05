package com.example.personal_management_app.repositories

import com.example.personal_management_app.entites.ReminderEntity
import com.example.personal_management_app.ui.theme.FabContainer
import com.example.personal_management_app.ui.theme.ReminderBlue
import com.example.personal_management_app.ui.theme.ReminderPink

class ReminderRepository {
    private val reminders = mutableListOf(
        ReminderEntity(
            id = "1",
            title = "Gọi chủ nhà",
            content = "Negotiate lease terms on rent increase.",
            timeText = "🕒 Ngày mai, 10:00 AM",
            backgroundColor = ReminderPink
        ),
        ReminderEntity(
            id = "2",
            title = "Hẹn kiểm tra xe",
            content = "Bring logbook and proof of insurance.",
            timeText = "🕒 Oct 12, 11:30 AM",
            backgroundColor = FabContainer
        ),
        ReminderEntity(
            id = "3",
            title = "Chuẩn bị họp tuần",
            content = "Tổng hợp số liệu\nCập nhật slide OKR",
            timeText = "🕒 T6day, 4:00 PM",
            backgroundColor = ReminderBlue,
            isCheckList = true
        )
    )

    fun getReminders(): List<ReminderEntity> {
        return reminders
    }
}