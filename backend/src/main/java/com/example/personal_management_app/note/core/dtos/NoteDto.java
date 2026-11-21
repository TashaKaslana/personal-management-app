package com.example.personal_management_app.note.core.dtos;

import lombok.Data;
import com.example.personal_management_app.note.core.enums.ReminderRepeat;
import com.example.personal_management_app.note.tag.dtos.TagDto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class NoteDto {
    private UUID id;
    private String title;
    private NoteTextStyleDto titleStyle;
    private String content;
    private NoteTextStyleDto contentStyle;
    private List<TagDto> tags = new ArrayList<>();
    private String backgroundColor;
    private boolean pinned;
    private OffsetDateTime archivedAt;
    private OffsetDateTime deletedAt;
    private OffsetDateTime reminderAt;
    private ReminderRepeat reminderRepeat;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}
