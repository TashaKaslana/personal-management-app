package com.example.personal_management_app.repositories

import com.example.personal_management_app.dtos.NoteTextStyle
import com.example.personal_management_app.entites.NoteEntity

class NoteRepository {
    private var notes = mutableListOf(
        NoteEntity(
            id = "1",
            title = "App Idea Sketch",
            titleStyle = NoteTextStyle(
                fontSize = 24f,
                bold = true
            ),
            content = "Xây dựng app ghi chú với giao diện Material You đẹp mắt.",
            contentStyle = NoteTextStyle(
                fontSize = 16f
            ),
            tag = "Công việc",
            backgroundColor = 0xFFFFF8D6,
            isPinned = true
        ),

        NoteEntity(
            id = "2",
            title = "Danh sách tạp hóa",
            titleStyle = NoteTextStyle(
                fontSize = 22f
            ),
            content = "• Táo hữu cơ\n• Almond Sữa\n• Sữa chua Hy Lạp",
            contentStyle = NoteTextStyle(
                fontSize = 16f
            ),
            backgroundColor = 0xFFE2F6ED
        ),

        NoteEntity(
            id = "3",
            title = "Du lịch itinerary",
            titleStyle = NoteTextStyle(
                fontSize = 24f,
                bold = true
            ),
            content = "Chi tiết lộ trình và đặt phòng cho chuyến đi Iceland.",
            contentStyle = NoteTextStyle(
                fontSize = 16f
            ),
            tag = "Du lịch",
            backgroundColor = 0xFFE3F2FD,
            isNotification = true,
            notificationCron = "0 0 8 * * ?"
        ),

        NoteEntity(
            id = "4",
            title = "Quy tắc thiết kế M3",
            titleStyle = NoteTextStyle(
                fontSize = 22f,
                bold = true
            ),
            content = "Đảm bảo bo góc 12px cho thẻ và nút FAB.",
            contentStyle = NoteTextStyle(
                fontSize = 16f,
                italic = true
            ),
            backgroundColor = 0xFFF1F8E9,
            isPinned = true
        ),

        NoteEntity(
            id = "5",
            title = "Nhắc nhở",
            titleStyle = NoteTextStyle(
                fontSize = 24f,
                bold = true
            ),
            content = "Call landlord to negotiate lease renew terms.\n🕒 Ngày mai, 10:00 AM",
            contentStyle = NoteTextStyle(
                fontSize = 16f
            ),
            backgroundColor = 0xFFFCE4EC,
            isNotification = true,
            notificationCron = "0 0 10 * * ?"
        ),

        NoteEntity(
            id = "6",
            title = "Workout Goals",
            titleStyle = NoteTextStyle(
                fontSize = 24f,
                bold = true
            ),
            content = "• Giãn cơ 10 phút\n• 6k Morning Run\n• Tập thể lực",
            contentStyle = NoteTextStyle(
                fontSize = 16f
            ),
            backgroundColor = 0xFFF3E5F5,
            isArchived = true
        )
    )

    fun insert(note: NoteEntity) {
        notes.add(note)
    }

    fun getList(): MutableList<NoteEntity> {
        return notes
    }

    fun delete(noteId: String) {
        notes.removeIf { it.id == noteId }
    }

    fun get(noteId: String): NoteEntity? {
        return notes.find { it.id == noteId }
    }

    fun update(updatedNote: NoteEntity) {
        val index = getItem(updatedNote)

        if (index != -1) {
            notes[index] = updatedNote
        }
    }

    fun pin(noteId: String, status: Boolean) {
        val index = getItem(noteId)

        notes[index] = notes[index].copy(
            isPinned = status
        )
    }

    fun setNotification(noteId: String, status: Boolean, cron: String) {
        val index = getItem(noteId)

        notes[index] = notes[index].copy(
            isNotification = status,
            notificationCron = cron
        )
    }

    fun setArchived(noteId: String, status: Boolean) {
        val index = getItem(noteId)

        notes[index] = notes[index].copy(
            isArchived = status
        )
    }

    private fun getItem(note: NoteEntity): Int {
        return notes.indexOfFirst { it.id == note.id }
    }

    private fun getItem(noteId: String): Int {
        return notes.indexOfFirst { it.id == noteId }
    }
}