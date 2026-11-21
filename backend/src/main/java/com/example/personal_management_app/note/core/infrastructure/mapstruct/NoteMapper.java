package com.example.personal_management_app.note.core.infrastructure.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.example.personal_management_app.note.core.dtos.CreateNoteRequest;
import com.example.personal_management_app.note.core.dtos.NoteDto;
import com.example.personal_management_app.note.core.infrastructure.persistence.entities.Note;

/**
 * Note mapper. Tag links are managed via {@link com.example.personal_management_app.note.core.service.NoteTagService}
 * and enriched by the service layer (Note has no tags collection on purpose - the join is an explicit entity).
 * Embedded text styles ({@code titleStyle}/{@code contentStyle}) are applied manually by the service
 * (null request style = defaults) and mapped automatically on the DTO side.
 */
@Mapper(componentModel = "spring")
public interface NoteMapper {

    @Mapping(target = "tags", ignore = true)
    NoteDto toDto(Note note);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "searchText", ignore = true)
    @Mapping(target = "titleStyle", ignore = true)
    @Mapping(target = "contentStyle", ignore = true)
    Note toEntity(CreateNoteRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "searchText", ignore = true)
    @Mapping(target = "titleStyle", ignore = true)
    @Mapping(target = "contentStyle", ignore = true)
    void updateEntity(CreateNoteRequest request, @MappingTarget Note note);
}
