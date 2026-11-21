package com.example.personal_management_app.note.tag.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTagRequest {
    @NotNull
    @Size(max = 100)
    private String name;

    @Size(max = 32)
    private String color;
}
