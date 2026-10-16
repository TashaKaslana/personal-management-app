package com.example.personal_management_app.repositories

import com.example.personal_management_app.dtos.NoteTextStyle
import com.example.personal_management_app.entites.NoteEntity
import com.example.personal_management_app.entites.NoteReminder
import com.example.personal_management_app.entites.ReminderRepeat
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor() {

    companion object {
        const val TRASH_RETENTION_MILLIS: Long = 30L * 24L * 60L * 60L * 1000L
    }

    private val seedArchivedAt: (Long) -> Long = { daysAgo ->
        System.currentTimeMillis() - daysAgo * 24L * 60L * 60L * 1000L
    }

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
            tags = listOf("Công việc"),
            backgroundColor = "0xFFFFF8D6",
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
            backgroundColor = "0xFFE2F6ED"
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
            tags = listOf("Du lịch", "Công việc"),
            backgroundColor = "0xFFE3F2FD",
            reminder = NoteReminder(
                atMillis = seedAt(daysAhead = 1, hour = 8, minute = 0),
                repeat = ReminderRepeat.Daily
            )
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
            backgroundColor = "0xFFF1F8E9",
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
            backgroundColor = "0xFFFCE4EC",
            reminder = NoteReminder(
                atMillis = seedAt(daysAhead = 1, hour = 10, minute = 0),
                repeat = ReminderRepeat.Once
            )
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
            backgroundColor = "0xFFF3E5F5",
            archivedAt = seedArchivedAt(12)
        ),

        NoteEntity(
            id = "7",
            title = "Nhật ký du lịch Iceland",
            titleStyle = NoteTextStyle(
                fontSize = 24f,
                bold = true
            ),
            content = "Highlights: Skógafoss waterfall, Glacier lagoon walk. Need to backup photos.",
            contentStyle = NoteTextStyle(
                fontSize = 16f
            ),
            tags = listOf("Du lịch"),
            backgroundColor = "0xFFE3F2FD",
            archivedAt = seedArchivedAt(7)
        ),

        NoteEntity(
            id = "8",
            title = "Danh sách tìm nhà",
            titleStyle = NoteTextStyle(
                fontSize = 22f,
                bold = true
            ),
            content = "• Kiểm tra cách nhiệt\n• Kiểm tra áp lực nước\n• Lựa chọn tấm pin năng lượng",
            contentStyle = NoteTextStyle(
                fontSize = 16f
            ),
            backgroundColor = "0xFFE2F6ED",
            archivedAt = seedArchivedAt(30)
        ),

        NoteEntity(
            id = "9",
            title = "Q3 Reflection Ghi chú",
            titleStyle = NoteTextStyle(
                fontSize = 22f,
                bold = true
            ),
            content = "Felt good about app development. Need more practice with Material 3 spacing dynamics.",
            contentStyle = NoteTextStyle(
                fontSize = 16f
            ),
            backgroundColor = "0xFFF3E5F5",
            archivedAt = seedArchivedAt(3)
        ),

        NoteEntity(
            id = "10",
            title = "Ý tưởng dự án cũ",
            titleStyle = NoteTextStyle(
                fontSize = 24f,
                bold = true
            ),
            content = "Xóa các module không cần thiết trong Jetpack Compose.",
            contentStyle = NoteTextStyle(
                fontSize = 16f
            ),
            backgroundColor = "0xFFFCE4EC",
            deletedAt = seedArchivedAt(2)
        ),

        NoteEntity(
            id = "11",
            title = "Ghi chú mua sắm",
            titleStyle = NoteTextStyle(
                fontSize = 24f,
                bold = true
            ),
            content = "Sữa, bánh mì, cà phê đen.",
            contentStyle = NoteTextStyle(
                fontSize = 16f
            ),
            backgroundColor = "0xFFE8F5E9",
            deletedAt = seedArchivedAt(5)
        )
    )

    fun insert(note: NoteEntity) {
        notes.add(note)
    }

    fun getList(): MutableList<NoteEntity> {
        purgeExpiredTrash()
        return notes.filter { it.deletedAt == null }.toMutableList()
    }

    fun getTrashItems(query: String = ""): List<NoteEntity> {
        purgeExpiredTrash()
        return notes.filter { note ->
            note.deletedAt != null && (
                query.isBlank() ||
                    note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true) ||
                    note.tags.any { it.contains(query, ignoreCase = true) }
                )
        }
    }

    fun delete(noteId: String) {
        val index = getItem(noteId)
        if (index != -1) {
            notes[index] = notes[index].copy(
                deletedAt = System.currentTimeMillis(),
                isPinned = false,
                archivedAt = null
            )
        }
    }

    fun restore(noteId: String) {
        val index = getItem(noteId)
        if (index != -1) {
            notes[index] = notes[index].copy(deletedAt = null)
        }
    }

    fun deleteForever(noteId: String) {
        notes.removeIf { it.id == noteId }
    }

    fun emptyTrash() {
        notes.removeIf { it.deletedAt != null }
    }

    fun purgeExpiredTrash() {
        val threshold = System.currentTimeMillis() - TRASH_RETENTION_MILLIS
        notes.removeIf { it.deletedAt != null && it.deletedAt < threshold }
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
        if (index != -1) {
            notes[index] = notes[index].copy(
                isPinned = status
            )
        }
    }

    fun setReminder(noteId: String, reminder: NoteReminder?) {
        val index = getItem(noteId)
        if (index != -1) {
            notes[index] = notes[index].copy(reminder = reminder)
        }
    }

    private fun seedAt(daysAhead: Int, hour: Int, minute: Int): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, daysAhead)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun countByTag(name: String): Int {
        return notes.count { it.deletedAt == null && name in it.tags }
    }

    fun renameTag(from: String, to: String) {
        if (from == to) return
        notes.replaceAll { note ->
            if (from !in note.tags) note
            else note.copy(tags = note.tags.map { if (it == from) to else it }.distinct())
        }
    }

    fun clearTag(name: String) {
        notes.replaceAll { note ->
            if (name !in note.tags) note
            else note.copy(tags = note.tags.filterNot { it == name })
        }
    }

    fun setArchived(noteId: String, archivedAt: Long?) {
        val index = getItem(noteId)
        if (index != -1) {
            notes[index] = notes[index].copy(
                archivedAt = archivedAt
            )
        }
    }

    private fun getItem(note: NoteEntity): Int {
        return notes.indexOfFirst { it.id == note.id }
    }

    private fun getItem(noteId: String): Int {
        return notes.indexOfFirst { it.id == noteId }
    }
}