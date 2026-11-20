package com.example.personal_management_app.user.core.infrastructure.mapstruct;

import com.example.personal_management_app.user.core.dtos.UpdateUserSettingsRequest;
import com.example.personal_management_app.user.core.dtos.UserSettingsDto;
import com.example.personal_management_app.user.core.infrastructure.persistence.entities.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface UserSettingsMapper {
    UserSettingsDto toDto(User user);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "settings", source = "settings")
    void updateEntity(UpdateUserSettingsRequest request, @MappingTarget User user);
}
