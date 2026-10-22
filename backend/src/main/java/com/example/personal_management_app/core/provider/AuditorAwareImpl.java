package com.example.personal_management_app.core.provider;

import org.springframework.data.domain.AuditorAware;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<UUID> {
    @Override
    @NonNull
    public Optional<UUID> getCurrentAuditor() {
        return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }
}

