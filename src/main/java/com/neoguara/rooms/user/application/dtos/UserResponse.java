package com.neoguara.rooms.user.application.dtos;

import com.neoguara.rooms.user.domain.enums.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role
) {}
