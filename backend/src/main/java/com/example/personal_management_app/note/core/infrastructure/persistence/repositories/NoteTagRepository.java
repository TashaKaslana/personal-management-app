package com.example.personal_management_app.note.core.infrastructure.persistence.repositories;

import com.example.personal_management_app.note.core.infrastructure.persistence.entities.NoteTag;
import com.example.personal_management_app.note.core.infrastructure.persistence.key.NoteTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NoteTagRepository extends JpaRepository<NoteTag, NoteTagId> {

    // Find all tag links for a note
    List<NoteTag> findByIdNoteId(UUID noteId);

    // Find all note links for a tag
    List<NoteTag> findByIdTagId(UUID tagId);

    // Bulk: find all tag links for multiple notes (with tags fetched)
    @Query("SELECT nt FROM NoteTag nt JOIN FETCH nt.tag WHERE nt.id.noteId IN :noteIds")
    List<NoteTag> findByIdNoteIdIn(@Param("noteIds") List<UUID> noteIds);

    // Count links for a tag
    long countByIdTagId(UUID tagId);

    // Bulk deletes
    @Modifying
    @Query("DELETE FROM NoteTag nt WHERE nt.id.noteId = :noteId")
    int deleteAllByIdNoteId(@Param("noteId") UUID noteId);

    @Modifying
    @Query("DELETE FROM NoteTag nt WHERE nt.id.tagId = :tagId")
    int deleteAllByIdTagId(@Param("tagId") UUID tagId);

    @Modifying
    @Query("DELETE FROM NoteTag nt WHERE nt.id.noteId IN :noteIds")
    int deleteAllByIdNoteIdIn(@Param("noteIds") List<UUID> noteIds);
}
