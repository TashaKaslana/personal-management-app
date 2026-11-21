package com.example.personal_management_app.note.core.exception;

import com.example.personal_management_app.note.core.enums.NoteError;

public class StaleNoteVersionException extends NoteDomainException {
    public StaleNoteVersionException() {
        super(NoteError.STALE_VERSION, "Reload it and reconcile your edits before saving.");
    }
}
