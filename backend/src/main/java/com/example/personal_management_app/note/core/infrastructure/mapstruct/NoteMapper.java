package com.example.personal_management_app.note.core.infrastructure.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import com.example.personal_management_app.note.core.dtos.NoteTextStyleDto;
import com.example.personal_management_app.note.core.infrastructure.persistence.entities.NoteTextStyle;
import com.example.personal_management_app.note.tag.dtos.TagDto;
import java.util.List;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.example.personal_management_app.note.core.dtos.CreateNoteRequest;
import com.example.personal_management_app.note.core.dtos.NoteDto;
import com.example.personal_management_app.note.core.infrastructure.persistence.entities.Note;

@Mapper(componentModel = "spring", imports = NoteTextStyle.class)
public interface NoteMapper {

    @Mapping(target = "tags", ignore = true)
    NoteDto toDto(Note note);

    NoteDto toDto(Note note, List<TagDto> tags);

    NoteTextStyle toStyle(NoteTextStyleDto style);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "searchText", ignore = true)
    @Mapping(target = "titleStyle", source = "titleStyle", defaultExpression = "java(new NoteTextStyle())")
    @Mapping(target = "contentStyle", source = "contentStyle", defaultExpression = "java(new NoteTextStyle())")
    Note toEntity(CreateNoteRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(CreateNoteRequest request, @MappingTarget Note note);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "backgroundColor", source = "backgroundColor")
    @Mapping(target = "titleStyle", source = "titleStyle", defaultExpression = "java(new NoteTextStyle())")
    @Mapping(target = "contentStyle", source = "contentStyle", defaultExpression = "java(new NoteTextStyle())")
    void updateEditorFields(CreateNoteRequest request, @MappingTarget Note note);
}
