package com.example.personal_management_app.note.core.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNoteRequest extends CreateNoteRequest {
    @NotNull
    @PositiveOrZero
    private Long version;
}
