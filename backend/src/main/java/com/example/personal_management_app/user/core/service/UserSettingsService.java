package com.example.personal_management_app.user.core.service;

import com.example.personal_management_app.user.core.dtos.UpdateUserSettingsRequest;
import com.example.personal_management_app.user.core.dtos.UserSettingsDto;
import com.example.personal_management_app.user.core.exception.UserNotFoundException;
import com.example.personal_management_app.user.core.infrastructure.mapstruct.UserSettingsMapper;
import com.example.personal_management_app.user.core.infrastructure.persistence.entities.User;
import com.example.personal_management_app.user.core.infrastructure.persistence.repositories.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSettingsService {

    private final UserRepository userRepository;
    private final UserSettingsMapper userSettingsMapper;

    public UserSettingsDto getSettings(UUID userId) {
        return userSettingsMapper.toDto(findUser(userId));
    }

    @Transactional
    public UserSettingsDto updateSettings(UUID userId, UpdateUserSettingsRequest request) {
        User user = findUser(userId);
        userSettingsMapper.updateEntity(request, user);
        return userSettingsMapper.toDto(userRepository.save(user));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
    }
}
