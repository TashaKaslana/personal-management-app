package com.example.personal_management_app.note.core.infrastructure.persistence.repositories;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.personal_management_app.note.core.infrastructure.persistence.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {

    @Query("select n.id from Note n where n.createdBy is not null and n.deletedAt is null and n.reminderAt <= :now order by n.reminderAt")
    List<UUID> findDueReminderIds(@Param("now") OffsetDateTime now);

    @Query(value = """
            select * from notes where id = :id and deleted_at is null and reminder_at <= :now
            for update skip locked
            """, nativeQuery = true)
    Optional<Note> lockDueReminder(@Param("id") UUID id, @Param("now") OffsetDateTime now);

    String VISIBLE = "(s.createdBy = :actor or (s.deletedAt is null and exists "
            + "(select 1 from NoteCollaborator member "
            + "where member.id.noteId = s.id and member.id.userId = :actor)))";

    @Query("select s from Note s where " + VISIBLE)
    Page<Note> findVisible(UUID actor, Pageable pageable);

    @Query("select count(s) from Note s where " + VISIBLE)
    long countVisible(UUID actor);

    // --- Active (not trashed) notes ---

    @Query("SELECT s FROM Note s WHERE " + VISIBLE + " AND s.deletedAt IS NULL ORDER BY s.pinned DESC, s.updatedAt DESC")
    List<Note> findAllActive(UUID actor);

    @Query("SELECT s FROM Note s WHERE " + VISIBLE + " AND s.deletedAt IS NULL AND s.archivedAt IS NULL ORDER BY s.pinned DESC, s.updatedAt DESC")
    List<Note> findAllUnarchived(UUID actor);

    @Query("SELECT s FROM Note s WHERE " + VISIBLE + " AND s.deletedAt IS NULL AND s.archivedAt IS NOT NULL ORDER BY s.updatedAt DESC")
    List<Note> findAllArchived(UUID actor);

    @Query("SELECT s FROM Note s WHERE " + VISIBLE + " AND s.deletedAt IS NULL AND s.pinned = true ORDER BY s.updatedAt DESC")
    List<Note> findAllPinned(UUID actor);

    // --- Trash ---

    @Query("SELECT s FROM Note s WHERE s.createdBy = :actor AND s.deletedAt IS NOT NULL ORDER BY s.deletedAt DESC")
    List<Note> findAllTrashed(UUID actor);

    List<Note> findByCreatedByAndDeletedAtBefore(UUID actor, OffsetDateTime threshold);

    // --- Search (visible text via search_text, or tag name) ---

    @Query("""
            SELECT DISTINCT s FROM Note s
            LEFT JOIN NoteTag nt ON nt.id.noteId = s.id
            LEFT JOIN nt.tag t
            WHERE""" + VISIBLE + """
              AND s.deletedAt IS NULL
              AND (LOWER(s.searchText) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')))
            ORDER BY s.pinned DESC, s.updatedAt DESC
            """)
    List<Note> searchActive(@Param("actor") UUID actor, @Param("query") String query);

    @Query("""
            SELECT DISTINCT s FROM Note s
            LEFT JOIN NoteTag nt ON nt.id.noteId = s.id
            LEFT JOIN nt.tag t
            WHERE s.createdBy = :actor AND s.deletedAt IS NOT NULL
              AND (LOWER(s.searchText) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')))
            ORDER BY s.deletedAt DESC
            """)
    List<Note> searchTrashed(@Param("actor") UUID actor, @Param("query") String query);

    // --- By tag ---

    @Query("""
            SELECT DISTINCT s FROM Note s
            JOIN NoteTag nt ON nt.id.noteId = s.id
            WHERE""" + VISIBLE + """
              AND nt.id.tagId = :tagId AND s.deletedAt IS NULL
            ORDER BY s.pinned DESC, s.updatedAt DESC
            """)
    List<Note> findActiveByTagId(@Param("actor") UUID actor, @Param("tagId") UUID tagId);

    // --- Counts ---

    @Query("select count(s) from Note s where " + VISIBLE + " and s.deletedAt is null")
    long countByDeletedAtIsNull(UUID actor);

    @Query("select count(s) from Note s where " + VISIBLE + " and s.deletedAt is null and s.archivedAt is not null")
    long countByDeletedAtIsNullAndArchivedAtIsNotNull(UUID actor);

    @Query("select count(s) from Note s where " + VISIBLE + " and s.deletedAt is not null")
    long countByDeletedAtIsNotNull(UUID actor);

    @Query("""
            SELECT COUNT(s) FROM Note s
            WHERE""" + VISIBLE + """
              AND s.deletedAt IS NULL
              AND s.id IN (SELECT nt.id.noteId FROM NoteTag nt WHERE nt.id.tagId = :tagId)
            """)
    long countActiveByTagId(@Param("actor") UUID actor, @Param("tagId") UUID tagId);
}
