package com.example.personal_management_app.repositories

import com.example.personal_management_app.core.service.SessionManager
import com.example.personal_management_app.data.local.dao.NoteDao
import com.example.personal_management_app.entities.NoteEntity
import com.example.personal_management_app.entities.NoteReminder
import com.example.personal_management_app.utils.matchesQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val sessionManager: SessionManager
) {
    companion object {
        const val TRASH_RETENTION_MILLIS: Long = 30L * 24L * 60L * 60L * 1000L
    }

    val currentUserId: String?
        get() = sessionManager.sessionState.value?.userId

    /**
     * Single Source of Truth (SSOT): UI luôn đọc dữ liệu từ Room Database qua Flow.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getNotesFlow(): Flow<List<NoteEntity>> = sessionManager.sessionState.flatMapLatest { session ->
        val uid = session?.userId
        if (uid != null) {
            noteDao.getNotesFlowByUserId(uid)
        } else {
            flowOf(emptyList())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTrashFlow(): Flow<List<NoteEntity>> = sessionManager.sessionState.flatMapLatest { session ->
        val uid = session?.userId
        if (uid != null) {
            purgeExpiredTrash()
            noteDao.getTrashNotesFlow(uid)
        } else {
            flowOf(emptyList())
        }
    }

    /**
     * Luồng Offline-First:
     * - Khi Online (có API): Gọi server -> lưu vào Room DB -> Room Flow tự động bắn lên UI.
     * - Khi Offline: Bỏ qua lỗi mạng, Room DB vẫn giữ dữ liệu và hiển thị bình thường.
     */
    suspend fun syncNotesFromRemote() {
        try {
            // Khi có API backend, gắn lệnh gọi Retrofit tại đây
        } catch (_: Exception) {
            // Chế độ Offline: Không làm gián đoạn ứng dụng, Room DB vẫn hoạt động trơn tru
        }
    }

    fun insert(note: NoteEntity) {
        val uid = currentUserId ?: return
        val noteToSave = note.copy(userId = uid)
        noteDao.insertNote(noteToSave)
    }

    fun getList(): MutableList<NoteEntity> {
        val uid = currentUserId ?: return mutableListOf()
        purgeExpiredTrash()
        return noteDao.getNotesByUserId(uid).toMutableList()
    }

    fun getTrashItems(query: String = ""): List<NoteEntity> {
        val uid = currentUserId ?: return emptyList()
        purgeExpiredTrash()
        val trashNotes = noteDao.getTrashNotes(uid)
        return if (query.isBlank()) {
            trashNotes
        } else {
            trashNotes.filter { it.matchesQuery(query) }
        }
    }

    fun delete(noteId: String) {
        val note = noteDao.getNoteById(noteId) ?: return
        noteDao.updateNote(
            note.copy(
                deletedAt = System.currentTimeMillis(),
                isPinned = false,
                archivedAt = null
            )
        )
    }

    fun restore(noteId: String) {
        val note = noteDao.getNoteById(noteId) ?: return
        noteDao.updateNote(note.copy(deletedAt = null))
    }

    fun deleteForever(noteId: String) {
        noteDao.deleteNoteById(noteId)
    }

    fun emptyTrash() {
        val uid = currentUserId ?: return
        noteDao.emptyTrash(uid)
    }

    fun purgeExpiredTrash() {
        val uid = currentUserId ?: return
        val threshold = System.currentTimeMillis() - TRASH_RETENTION_MILLIS
        noteDao.purgeExpiredTrash(uid, threshold)
    }

    fun get(noteId: String): NoteEntity? {
        return noteDao.getNoteById(noteId)
    }

    fun update(updatedNote: NoteEntity) {
        val uid = currentUserId ?: return
        noteDao.updateNote(updatedNote.copy(userId = uid))
    }

    fun pin(noteId: String, status: Boolean) {
        noteDao.pinNote(noteId, status)
    }

    fun setReminder(noteId: String, reminder: NoteReminder?) {
        val note = noteDao.getNoteById(noteId) ?: return
        noteDao.updateNote(note.copy(reminder = reminder))
    }

    fun countByTag(name: String): Int {
        val uid = currentUserId ?: return 0
        return noteDao.getNotesByUserId(uid).count { name in it.tags }
    }

    fun renameTag(from: String, to: String) {
        if (from == to) return
        val uid = currentUserId ?: return
        val allNotes = noteDao.getAllRaw(uid)
        allNotes.forEach { note ->
            if (from in note.tags) {
                val updatedTags = note.tags.map { if (it == from) to else it }.distinct()
                noteDao.updateNote(note.copy(tags = updatedTags))
            }
        }
    }

    fun clearTag(name: String) {
        val uid = currentUserId ?: return
        val allNotes = noteDao.getAllRaw(uid)
        allNotes.forEach { note ->
            if (name in note.tags) {
                val updatedTags = note.tags.filterNot { it == name }
                noteDao.updateNote(note.copy(tags = updatedTags))
            }
        }
    }

    fun setArchived(noteId: String, archivedAt: Long?) {
        noteDao.setArchived(noteId, archivedAt)
    }
}
