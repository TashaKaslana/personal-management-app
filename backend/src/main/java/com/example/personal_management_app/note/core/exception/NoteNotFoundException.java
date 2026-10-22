package com.example.personal_management_app.note.core.exception;

import com.example.personal_management_app.note.core.enums.NoteError;

/**
 * Exception thrown when a note is not found
 */
public class NoteNotFoundException extends NoteDomainException {
    public NoteNotFoundException(String noteId) {
        super(NoteError.NOTE_NOT_FOUND, noteId);
    }

    public NoteNotFoundException(String property, String value) {
        super(NoteError.NOTE_NOT_FOUND, property + ": " + value);
    }
}
