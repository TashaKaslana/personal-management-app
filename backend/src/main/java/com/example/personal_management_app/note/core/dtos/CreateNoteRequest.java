package com.example.personal_management_app.note.core.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.example.personal_management_app.note.core.enums.ReminderRepeat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Create/update a note. Update is a full replace (same semantics as the frontend
 * NoteRepository. Update which swaps the whole entity).
 * <ul>
 *     <li>{@code tagIds} - optional; when null on update, existing tags are kept</li>
 *     <li>{@code titleStyle}/{@code contentStyle} - optional; when null the defaults are applied</li>
 *     <li>{@code reminderAt}/{@code reminderRepeat} - nullable; null clears the reminder</li>
 * </ul>
 */
@Data
public class CreateNoteRequest {
    @NotNull
    private String title;

    @NotNull
    private String content;

    private NoteTextStyleDto titleStyle;

    private NoteTextStyleDto contentStyle;

    private String backgroundColor;

    private Boolean pinned = false;

    private OffsetDateTime reminderAt;

    private ReminderRepeat reminderRepeat;

    private List<UUID> tagIds;
}
