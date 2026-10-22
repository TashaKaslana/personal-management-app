package com.example.personal_management_app.note.core.enums;

import lombok.Getter;

/**
 * Enumeration of note-specific error messages
 */
@Getter
public enum NoteError {
    NOTE_NOT_FOUND("note.not_found", "Note not found"),
    TAG_ALREADY_ASSIGNED("note.tag_already_assigned", "Tag already assigned to note"),
    TAG_NOT_ASSIGNED("note.tag_not_assigned", "Tag not assigned to note");

    private final String code;
    private final String message;

    NoteError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Format error message with additional details
     * @param details Additional details to append to the message
     * @return Formatted error message
     */
    public String formatMessage(String details) {
        return this.message + ": " + details;
    }
}
