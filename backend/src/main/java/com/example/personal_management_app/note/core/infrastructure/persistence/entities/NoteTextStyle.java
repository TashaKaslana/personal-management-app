package com.example.personal_management_app.note.core.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * Text style applied to a note title or body.
 * Embedded twice on {@link Note} (title style and content style).
 */
@Getter
@Setter
@Embeddable
public class NoteTextStyle {

    public static final float DEFAULT_FONT_SIZE = 16f;

    @Column(name = "font_size", nullable = false)
    private float fontSize = DEFAULT_FONT_SIZE;

    @Column(name = "bold", nullable = false)
    private boolean bold = false;

    @Column(name = "italic", nullable = false)
    private boolean italic = false;

    @Column(name = "underline", nullable = false)
    private boolean underline = false;

    /**
     * Reset the style back to its defaults
     */
    public void reset() {
        this.fontSize = DEFAULT_FONT_SIZE;
        this.bold = false;
        this.italic = false;
        this.underline = false;
    }
}
