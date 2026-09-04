package com.example.personal_management_app.repositories

import androidx.compose.ui.graphics.Color
import com.example.personal_management_app.entites.NoteEntity

class NoteRepository {
    private val notes = mutableListOf<NoteEntity>(
        NoteEntity("1", "App Idea Sketch", "Xây dựng app ghi chú với giao diện Material You đẹp mắt.", "Công việc", Color(0xFFFFF8D6)),
        NoteEntity("2", "Danh sách tạp hóa", "• Táo hữu cơ\n• Almond Sữa\n• Sữa chua Hy Lạp", null, Color(0xFFE2F6ED)),
        NoteEntity("3", "Du lịch itinerary", "Chi tiết lộ trình và đặt phòng cho chuyến đi Iceland.", "Du lịch", Color(0xFFE3F2FD)),
        NoteEntity("4", "Quy tắc thiết kế M3", "Đảm bảo bo góc 12px cho thẻ và nút FAB.", null, Color(0xFFF1F8E9)),
        NoteEntity("5", "Nhắc nhở", "Call landlord to negotiate lease renew terms.\n🕒 Ngày mai, 10:00 AM", null, Color(0xFFFCE4EC)),
        NoteEntity("6", "Công việcout Goals", "• Giãn cơ 10 phút\n• 6k Morning Run\n• Tập thể lực", null, Color(0xFFF3E5F5))
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
        notes.find { it.id == updatedNote.id }?.let {
            it.title = updatedNote.title
            it.content = updatedNote.content
            it.tag = updatedNote.tag
            it.backgroundColor = updatedNote.backgroundColor
        }
    }
}