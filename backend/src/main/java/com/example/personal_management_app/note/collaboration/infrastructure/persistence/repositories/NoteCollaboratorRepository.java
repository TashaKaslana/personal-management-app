package com.example.personal_management_app.note.collaboration.infrastructure.persistence.repositories;

import com.example.personal_management_app.note.collaboration.infrastructure.persistence.entities.NoteCollaborator;
import com.example.personal_management_app.note.collaboration.infrastructure.persistence.key.NoteCollaboratorId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NoteCollaboratorRepository extends JpaRepository<NoteCollaborator, NoteCollaboratorId> {
    List<NoteCollaborator> findByIdNoteId(UUID noteId);
}
