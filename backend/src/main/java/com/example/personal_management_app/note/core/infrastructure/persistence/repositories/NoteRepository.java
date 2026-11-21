package com.example.personal_management_app.note.core.infrastructure.persistence.repositories;

import com.example.personal_management_app.note.core.infrastructure.persistence.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {

    // --- Active (not trashed) notes ---

    @Query("SELECT s FROM Note s WHERE s.deletedAt IS NULL ORDER BY s.pinned DESC, s.updatedAt DESC")
    List<Note> findAllActive();

    @Query("SELECT s FROM Note s WHERE s.deletedAt IS NULL AND s.archivedAt IS NULL ORDER BY s.pinned DESC, s.updatedAt DESC")
    List<Note> findAllUnarchived();

    @Query("SELECT s FROM Note s WHERE s.deletedAt IS NULL AND s.archivedAt IS NOT NULL ORDER BY s.updatedAt DESC")
    List<Note> findAllArchived();

    @Query("SELECT s FROM Note s WHERE s.deletedAt IS NULL AND s.pinned = true ORDER BY s.updatedAt DESC")
    List<Note> findAllPinned();

    // --- Trash ---

    @Query("SELECT s FROM Note s WHERE s.deletedAt IS NOT NULL ORDER BY s.deletedAt DESC")
    List<Note> findAllTrashed();

    List<Note> findByDeletedAtBefore(OffsetDateTime threshold);

    // --- Search (visible text via search_text, or tag name) ---

    @Query("""
            SELECT DISTINCT s FROM Note s
            LEFT JOIN NoteTag nt ON nt.id.noteId = s.id
            LEFT JOIN nt.tag t
            WHERE s.deletedAt IS NULL
              AND (LOWER(s.searchText) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')))
            ORDER BY s.pinned DESC, s.updatedAt DESC
            """)
    List<Note> searchActive(@Param("query") String query);

    @Query("""
            SELECT DISTINCT s FROM Note s
            LEFT JOIN NoteTag nt ON nt.id.noteId = s.id
            LEFT JOIN nt.tag t
            WHERE s.deletedAt IS NOT NULL
              AND (LOWER(s.searchText) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')))
            ORDER BY s.deletedAt DESC
            """)
    List<Note> searchTrashed(@Param("query") String query);

    // --- By tag ---

    @Query("""
            SELECT DISTINCT s FROM Note s
            JOIN NoteTag nt ON nt.id.noteId = s.id
            WHERE nt.id.tagId = :tagId AND s.deletedAt IS NULL
            ORDER BY s.pinned DESC, s.updatedAt DESC
            """)
    List<Note> findActiveByTagId(@Param("tagId") UUID tagId);

    // --- Counts ---

    long countByDeletedAtIsNull();

    long countByDeletedAtIsNullAndArchivedAtIsNotNull();

    long countByDeletedAtIsNotNull();

    @Query("""
            SELECT COUNT(s) FROM Note s
            WHERE s.deletedAt IS NULL
              AND s.id IN (SELECT nt.id.noteId FROM NoteTag nt WHERE nt.id.tagId = :tagId)
            """)
    long countActiveByTagId(@Param("tagId") UUID tagId);
}
