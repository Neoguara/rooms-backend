package com.neoguara.rooms.auth.application.ports;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuthUserData(
        UUID id,
        String name,
        String email,
        String encodedPassword,
        String role,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
