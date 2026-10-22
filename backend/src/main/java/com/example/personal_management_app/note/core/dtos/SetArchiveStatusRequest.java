package com.example.personal_management_app.note.core.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetArchiveStatusRequest {
    @NotNull
    private Boolean archived;
}
