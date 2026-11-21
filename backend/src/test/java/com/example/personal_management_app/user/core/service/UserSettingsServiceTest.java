package com.example.personal_management_app.user.core.service;

import com.example.personal_management_app.user.core.dtos.UpdateUserSettingsRequest;
import com.example.personal_management_app.user.core.exception.UserNotFoundException;
import com.example.personal_management_app.user.core.infrastructure.mapstruct.UserSettingsMapper;
import com.example.personal_management_app.user.core.infrastructure.persistence.entities.User;
import com.example.personal_management_app.user.core.infrastructure.persistence.repositories.UserRepository;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSettingsServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserSettingsMapper userSettingsMapper = Mappers.getMapper(UserSettingsMapper.class);
    @InjectMocks
    private UserSettingsService service;

    @Test
    void getSettings_shouldReturnEmptyObject_whenUserHasNoSettings() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(new User()));

        assertThat(service.getSettings(id).settings()).isEmpty();
    }

    @Test
    void updateSettings_shouldReplaceSettings_whenRequestContainsNestedValues() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setSettings(Map.of("old", true));
        Map<String, Object> settings = Map.of("theme", "dark", "notifications", Map.of("email", false));
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        var result = service.updateSettings(id, new UpdateUserSettingsRequest(settings));

        assertThat(result.settings()).isEqualTo(settings).doesNotContainKey("old");
        verify(userRepository).save(user);
    }

    @Test
    void updateSettings_shouldClearSettings_whenObjectIsEmpty() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setSettings(Map.of("theme", "dark"));
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        assertThat(service.updateSettings(id, new UpdateUserSettingsRequest(Map.of())).settings()).isEmpty();
    }

    @Test
    void getSettings_shouldThrow_whenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSettings(id)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateSettings_shouldThrow_whenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateSettings(id, new UpdateUserSettingsRequest(Map.of())))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository, never()).save(any());
    }
}
