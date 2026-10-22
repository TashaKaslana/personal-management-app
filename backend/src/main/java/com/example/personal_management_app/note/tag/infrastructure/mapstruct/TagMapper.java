package com.example.personal_management_app.note.tag.infrastructure.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.example.personal_management_app.note.tag.dtos.CreateTagRequest;
import com.example.personal_management_app.note.tag.dtos.TagDto;
import com.example.personal_management_app.note.tag.infrastructure.persistence.entities.Tag;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagDto toDto(Tag tag);

    List<TagDto> toDtoList(List<Tag> tags);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Tag toEntity(CreateTagRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(CreateTagRequest request, @MappingTarget Tag tag);
}
