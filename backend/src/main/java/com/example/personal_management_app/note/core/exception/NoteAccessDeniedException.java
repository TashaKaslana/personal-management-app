package com.example.personal_management_app.note.core.exception;

import com.example.personal_management_app.note.core.enums.NoteError;

public class NoteAccessDeniedException extends NoteDomainException {
    public NoteAccessDeniedException(String reason) {
        super(NoteError.ACCESS_DENIED, reason);
    }
}
