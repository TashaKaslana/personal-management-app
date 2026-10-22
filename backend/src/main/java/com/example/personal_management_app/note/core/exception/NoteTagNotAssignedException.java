package com.example.personal_management_app.note.core.exception;

import com.example.personal_management_app.note.core.enums.NoteError;

/**
 * Exception thrown when a tag is not assigned to a note
 */
public class NoteTagNotAssignedException extends NoteDomainException {
    public NoteTagNotAssignedException(String noteId, String tagId) {
        super(NoteError.TAG_NOT_ASSIGNED, "noteId: " + noteId + ", tagId: " + tagId);
    }
}
