package com.example.personal_management_app.note.collaboration.service;

import com.example.personal_management_app.note.collaboration.enums.CollaboratorRole;
import com.example.personal_management_app.note.collaboration.infrastructure.persistence.key.NoteCollaboratorId;
import com.example.personal_management_app.note.collaboration.infrastructure.persistence.repositories.NoteCollaboratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteMembershipService {
    private final NoteCollaboratorRepository members;

    public boolean contains(UUID noteId, UUID userId) {
        return members.existsById(new NoteCollaboratorId(noteId, userId));
    }

    public boolean hasRole(UUID noteId, UUID userId, CollaboratorRole role) {
        return members.findById(new NoteCollaboratorId(noteId, userId))
                .filter(member -> member.getRole() == role).isPresent();
    }
}
