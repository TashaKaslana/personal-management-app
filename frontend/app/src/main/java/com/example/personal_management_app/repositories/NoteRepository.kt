package com.example.personal_management_app.repositories

import android.content.Context
import com.example.personal_management_app.core.service.SessionManager
import com.example.personal_management_app.data.local.dao.NoteDao
import com.example.personal_management_app.entities.NoteEntity
import com.example.personal_management_app.entities.NoteReminder
import com.example.personal_management_app.utils.exportDatabase
import com.example.personal_management_app.utils.matchesQuery
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) {
    companion object {
        const val TRASH_RETENTION_MILLIS: Long = 30L * 24L * 60L * 60L * 1000L
    }

    val currentUserId: String
        get() = sessionManager.sessionState.value?.userId ?: "user_01"

    /**
     * Single Source of Truth (SSOT): UI luôn đọc dữ liệu từ Room Database qua Flow.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getNotesFlow(): Flow<List<NoteEntity>> = sessionManager.sessionState.flatMapLatest { session ->
        val uid = session?.userId ?: "user_01"
        noteDao.getNotesFlowByUserId(uid)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTrashFlow(): Flow<List<NoteEntity>> = sessionManager.sessionState.flatMapLatest { session ->
        val uid = session?.userId ?: "user_01"
        purgeExpiredTrash()
        noteDao.getTrashNotesFlow(uid)
    }

    /**
     * Luồng Offline-First:
     * - Khi Online (có API): Gọi server -> lưu vào Room DB -> Room Flow tự động bắn lên UI.
     * - Khi Offline: Bỏ qua lỗi mạng, Room DB vẫn giữ dữ liệu và hiển thị bình thường.
     */
    suspend fun syncNotesFromRemote() {
        try {
            // Khi có API backend, gắn lệnh gọi Retrofit tại đây:
            // val remoteList = noteApiService.getNotes(currentUserId)
            // remoteList.forEach { noteDao.insertNote(it.toEntity(userId = currentUserId)) }
            // exportDatabase(context)
        } catch (_: Exception) {
            // Chế độ Offline: Không làm gián đoạn ứng dụng, Room DB vẫn hoạt động trơn tru
        }
    }

    fun insert(note: NoteEntity) {
        val noteToSave = note.copy(userId = currentUserId)
        noteDao.insertNote(noteToSave)
        exportDatabase(context)
    }

    fun getList(): MutableList<NoteEntity> {
        val uid = currentUserId ?: return mutableListOf()
        purgeExpiredTrash()
        return noteDao.getNotesByUserId(currentUserId).toMutableList()
    }

    fun getTrashItems(query: String = ""): List<NoteEntity> {
        val uid = currentUserId ?: return emptyList()
        purgeExpiredTrash()
        val trashNotes = noteDao.getTrashNotes(currentUserId)
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
        exportDatabase(context)
    }

    fun restore(noteId: String) {
        val note = noteDao.getNoteById(noteId) ?: return
        noteDao.updateNote(note.copy(deletedAt = null))
        exportDatabase(context)
    }

    fun deleteForever(noteId: String) {
        noteDao.deleteNoteById(noteId)
        exportDatabase(context)
    }

    fun emptyTrash() {
        noteDao.emptyTrash(currentUserId)
        exportDatabase(context)
    }

    fun purgeExpiredTrash() {
        val uid = currentUserId ?: return
        val threshold = System.currentTimeMillis() - TRASH_RETENTION_MILLIS
        noteDao.purgeExpiredTrash(currentUserId, threshold)
    }

    fun get(noteId: String): NoteEntity? {
        return noteDao.getNoteById(noteId)
    }

    fun update(updatedNote: NoteEntity) {
        noteDao.updateNote(updatedNote.copy(userId = currentUserId))
        exportDatabase(context)
    }

    fun pin(noteId: String, status: Boolean) {
        noteDao.pinNote(noteId, status)
        exportDatabase(context)
    }

    fun setReminder(noteId: String, reminder: NoteReminder?) {
        val note = noteDao.getNoteById(noteId) ?: return
        noteDao.updateNote(note.copy(reminder = reminder))
        exportDatabase(context)
    }

    fun countByTag(name: String): Int {
        return noteDao.getNotesByUserId(currentUserId).count { name in it.tags }
    }

    fun renameTag(from: String, to: String) {
        if (from == to) return
        val allNotes = noteDao.getAllRaw(currentUserId)
        allNotes.forEach { note ->
            if (from in note.tags) {
                val updatedTags = note.tags.map { if (it == from) to else it }.distinct()
                noteDao.updateNote(note.copy(tags = updatedTags))
            }
        }
        exportDatabase(context)
    }

    fun clearTag(name: String) {
        val allNotes = noteDao.getAllRaw(currentUserId)
        allNotes.forEach { note ->
            if (name in note.tags) {
                val updatedTags = note.tags.filterNot { it == name }
                noteDao.updateNote(note.copy(tags = updatedTags))
            }
        }
        exportDatabase(context)
    }

    fun setArchived(noteId: String, archivedAt: Long?) {
        noteDao.setArchived(noteId, archivedAt)
        exportDatabase(context)
    }
}
