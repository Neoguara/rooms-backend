package com.neoguara.rooms.auth;

import java.time.LocalDateTime;
import java.util.UUID;

public record TokenResponse(
        String token,
        UUID id,
        String name,
        String email,
        String role,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
