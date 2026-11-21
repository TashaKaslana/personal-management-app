package com.example.personal_management_app.core.provider;

import com.example.personal_management_app.core.security.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditorAwareImplTest {
    @Mock private CurrentUserProvider currentUserProvider;
    @InjectMocks private AuditorAwareImpl auditorAware;

    @Test
    void getCurrentAuditor_shouldUseCurrentIdentity_whenUserIsAvailable() {
        UUID userId = UUID.randomUUID();
        when(currentUserProvider.getCurrentUserId()).thenReturn(Optional.of(userId));

        assertThat(auditorAware.getCurrentAuditor()).contains(userId);
    }

    @Test
    void getCurrentAuditor_shouldRemainEmpty_whenNoIdentityIsAvailable() {
        when(currentUserProvider.getCurrentUserId()).thenReturn(Optional.empty());

        assertThat(auditorAware.getCurrentAuditor()).isEmpty();
    }
}
