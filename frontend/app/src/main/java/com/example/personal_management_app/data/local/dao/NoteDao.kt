package com.example.personal_management_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.personal_management_app.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE userId = :userId AND deletedAt IS NULL")
    fun getNotesFlowByUserId(userId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE userId = :userId AND deletedAt IS NULL")
    fun getNotesByUserId(userId: String): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE userId = :userId AND deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getTrashNotes(userId: String): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE userId = :userId AND deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getTrashNotesFlow(userId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun getNoteById(id: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun getNoteByIdFlow(id: String): Flow<NoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: NoteEntity)

    @Update
    fun updateNote(note: NoteEntity)

    @Delete
    fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    fun deleteNoteById(id: String)

    @Query("DELETE FROM notes WHERE userId = :userId AND deletedAt IS NOT NULL")
    fun emptyTrash(userId: String)

    @Query("DELETE FROM notes WHERE userId = :userId AND deletedAt IS NOT NULL AND deletedAt < :threshold")
    fun purgeExpiredTrash(userId: String, threshold: Long)

    @Query("UPDATE notes SET isPinned = :status WHERE id = :noteId")
    fun pinNote(noteId: String, status: Boolean)

    @Query("UPDATE notes SET archivedAt = :archivedAt WHERE id = :noteId")
    fun setArchived(noteId: String, archivedAt: Long?)

    @Query("SELECT * FROM notes WHERE userId = :userId")
    fun getAllRaw(userId: String): List<NoteEntity>
}
