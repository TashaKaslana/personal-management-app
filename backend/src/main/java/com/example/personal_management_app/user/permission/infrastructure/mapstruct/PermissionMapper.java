package com.example.personal_management_app.user.permission.infrastructure.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.example.personal_management_app.user.permission.dtos.CreatePermissionRequest;
import com.example.personal_management_app.user.permission.dtos.PermissionDto;
import com.example.personal_management_app.user.permission.infrastructure.persistence.entities.Permission;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionDto toDto(Permission permission);

    List<PermissionDto> toDtoList(List<Permission> permissions);

    @Mapping(target = "id", ignore = true)
    Permission toEntity(CreatePermissionRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(CreatePermissionRequest request, @MappingTarget Permission permission);
}
