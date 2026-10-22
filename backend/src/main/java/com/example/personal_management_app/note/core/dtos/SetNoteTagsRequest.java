package com.example.personal_management_app.note.core.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Replace the full tag set of a note with the given tag ids
 */
@Data
public class SetNoteTagsRequest {
    @NotNull
    private List<UUID> tagIds;
}
