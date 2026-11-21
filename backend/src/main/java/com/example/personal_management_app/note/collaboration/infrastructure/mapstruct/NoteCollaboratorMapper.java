package com.example.personal_management_app.note.collaboration.infrastructure.mapstruct;

import com.example.personal_management_app.note.collaboration.enums.CollaboratorRole;
import com.example.personal_management_app.note.collaboration.infrastructure.persistence.entities.NoteCollaborator;
import com.example.personal_management_app.note.collaboration.infrastructure.persistence.key.NoteCollaboratorId;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface NoteCollaboratorMapper {
    NoteCollaborator toEntity(NoteCollaboratorId id, CollaboratorRole role);

    default Map<UUID, CollaboratorRole> toRoleMap(List<NoteCollaborator> members) {
        return members.stream().collect(Collectors.toMap(member -> member.getId().userId(), NoteCollaborator::getRole));
    }
}
