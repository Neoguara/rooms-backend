package com.neoguara.rooms.auth.dto;

import com.neoguara.rooms.user.domain.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record TokenResponse(
        String token,
        UUID id,
        String name,
        String email,
        UserRole role,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
