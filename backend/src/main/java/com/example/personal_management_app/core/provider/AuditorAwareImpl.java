package com.example.personal_management_app.core.provider;

import org.springframework.data.domain.AuditorAware;
import com.example.personal_management_app.core.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("auditorProvider")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<UUID> {
    private final CurrentUserProvider currentUserProvider;

    @Override
    @NonNull
    public Optional<UUID> getCurrentAuditor() {
        return currentUserProvider.getCurrentUserId().filter(id -> !new UUID(0, 0).equals(id));
    }
}

