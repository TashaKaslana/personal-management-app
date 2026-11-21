package com.example.personal_management_app.note.tag.dtos;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TagDto {
    private UUID id;
    private String name;
    private String color;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}
