package com.example.personal_management_app.note.reminder.controller;

import com.example.personal_management_app.core.responses.RestApiResponse;
import com.example.personal_management_app.note.core.dtos.NoteDto;
import com.example.personal_management_app.note.core.service.NoteService;
import com.example.personal_management_app.note.reminder.dtos.SetReminderRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteReminderController {
    private final NoteService noteService;

    @PutMapping("/{id}/reminder")
    public ResponseEntity<RestApiResponse<NoteDto>> setNoteReminder(
            @PathVariable UUID id,
            @Valid @RequestBody SetReminderRequest request) {
        NoteDto note = noteService.setReminder(id, request.getAt(), request.getRepeat());
        return RestApiResponse.success(note, "Note reminder set successfully");
    }

    @DeleteMapping("/{id}/reminder")
    public ResponseEntity<RestApiResponse<NoteDto>> clearNoteReminder(@PathVariable UUID id) {
        NoteDto note = noteService.clearReminder(id);
        return RestApiResponse.success(note, "Note reminder cleared successfully");
    }
}