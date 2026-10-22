package com.example.personal_management_app.user.core.infrastructure.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.example.personal_management_app.user.core.dtos.CreateUserRequest;
import com.example.personal_management_app.user.core.dtos.UpdateUserRequest;
import com.example.personal_management_app.user.core.dtos.UserDto;
import com.example.personal_management_app.user.core.infrastructure.persistence.entities.User;
import com.example.personal_management_app.user.role.infrastructure.mapstruct.RoleMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class}, unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "role", source = "role")
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "passwordHash", source = "password")
    User toEntity(CreateUserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);
}
