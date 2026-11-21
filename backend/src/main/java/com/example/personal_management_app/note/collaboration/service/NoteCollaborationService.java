package com.example.personal_management_app.note.collaboration.service;

import com.example.personal_management_app.note.collaboration.enums.CollaboratorRole;
import com.example.personal_management_app.note.collaboration.infrastructure.persistence.entities.NoteCollaborator;
import com.example.personal_management_app.note.collaboration.infrastructure.persistence.key.NoteCollaboratorId;
import com.example.personal_management_app.note.collaboration.infrastructure.persistence.repositories.NoteCollaboratorRepository;
import com.example.personal_management_app.note.core.service.NoteService;
import com.example.personal_management_app.user.core.service.UserService;
import lombok.RequiredArgsConstructor;
import com.example.personal_management_app.note.core.exception.NoteAccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import com.example.personal_management_app.note.collaboration.infrastructure.mapstruct.NoteCollaboratorMapper;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteCollaborationService {
    private final NoteCollaboratorRepository members;
    private final NoteCollaboratorMapper mapper;
    private final NoteService notes;
    private final UserService users;

    public Map<UUID, CollaboratorRole> collaborators(UUID noteId) {
        notes.requireOwner(noteId);
        return mapper.toRoleMap(members.findByIdNoteId(noteId));
    }

    @Transactional
    public void setCollaborator(UUID noteId, UUID userId, CollaboratorRole role) {
        UUID owner = notes.lockOwnedNoteForSharing(noteId);
        if (owner.equals(userId)) {
            throw new NoteAccessDeniedException("The owner already has full access");
        }
        users.findById(userId);
        NoteCollaboratorId id = new NoteCollaboratorId(noteId, userId);
        if (members.findById(id).map(NoteCollaborator::getRole).orElse(null) != role) {
            members.save(mapper.toEntity(id, role));
            notes.advanceSharingVersion(noteId);
        }
    }

    @Transactional
    public void removeCollaborator(UUID noteId, UUID userId) {
        notes.lockOwnedNoteForSharing(noteId);
        members.findById(new NoteCollaboratorId(noteId, userId)).ifPresent(member -> {
            members.delete(member);
            notes.advanceSharingVersion(noteId);
        });
    }
}
