package com.example.personal_management_app.note.core.dtos;

import lombok.Data;

/**
 * Text style for a note title or body (mirrors the frontend NoteTextStyle)
 */
@Data
public class NoteTextStyleDto {
    private float fontSize = 16f;
    private boolean bold = false;
    private boolean italic = false;
    private boolean underline = false;
}
