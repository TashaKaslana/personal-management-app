package com.example.personal_management_app.note.collaboration.controller;

import com.example.personal_management_app.core.responses.RestApiResponse;
import com.example.personal_management_app.note.collaboration.enums.CollaboratorRole;
import com.example.personal_management_app.note.collaboration.service.NoteCollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notes/{id}/collaborators")
@RequiredArgsConstructor
public class NoteCollaboratorController {
    private final NoteCollaborationService collaboration;

    @GetMapping
    public ResponseEntity<RestApiResponse<Map<UUID, CollaboratorRole>>> collaborators(@PathVariable UUID id) {
        return RestApiResponse.success(collaboration.collaborators(id), "Note collaborators retrieved");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<RestApiResponse<Void>> setCollaborator(@PathVariable UUID id, @PathVariable UUID userId,
                                                                 @RequestParam CollaboratorRole role) {
        collaboration.setCollaborator(id, userId, role);
        return RestApiResponse.success("Note collaborator saved");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<RestApiResponse<Void>> removeCollaborator(@PathVariable UUID id, @PathVariable UUID userId) {
        collaboration.removeCollaborator(id, userId);
        return RestApiResponse.success("Note collaborator removed");
    }
}
