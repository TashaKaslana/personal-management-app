package com.example.personal_management_app.note.core.exception;

import com.example.personal_management_app.note.core.enums.NoteError;

/**
 * Exception thrown when a tag is already assigned to a note
 */
public class NoteTagAlreadyAssignedException extends NoteDomainException {
    public NoteTagAlreadyAssignedException(String noteId, String tagId) {
        super(NoteError.TAG_ALREADY_ASSIGNED, "noteId: " + noteId + ", tagId: " + tagId);
    }
}
