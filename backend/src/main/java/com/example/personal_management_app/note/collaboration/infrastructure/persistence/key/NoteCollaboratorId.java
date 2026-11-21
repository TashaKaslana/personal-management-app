package com.example.personal_management_app.note.collaboration.infrastructure.persistence.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record NoteCollaboratorId(
        @Column(name = "note_id", nullable = false) UUID noteId,
        @Column(name = "user_id", nullable = false) UUID userId
) implements Serializable {}
