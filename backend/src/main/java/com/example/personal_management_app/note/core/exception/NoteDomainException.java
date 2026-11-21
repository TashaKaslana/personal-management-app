package com.example.personal_management_app.note.core.exception;

import lombok.Getter;
import com.example.personal_management_app.note.core.enums.NoteError;

@Getter
public class NoteDomainException extends RuntimeException {
    private final NoteError error;

    public NoteDomainException(NoteError error, String details) {
        super(error.formatMessage(details));
        this.error = error;
    }
}
