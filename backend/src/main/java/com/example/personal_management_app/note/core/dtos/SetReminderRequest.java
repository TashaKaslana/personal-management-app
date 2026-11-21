package com.example.personal_management_app.note.core.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.example.personal_management_app.note.core.enums.ReminderRepeat;

import java.time.OffsetDateTime;

@Data
public class SetReminderRequest {
    @NotNull
    private OffsetDateTime at;

    private ReminderRepeat repeat;
}
