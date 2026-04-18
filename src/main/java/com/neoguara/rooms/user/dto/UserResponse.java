package com.neoguara.rooms.user.dto;

import com.neoguara.rooms.user.User;
import com.neoguara.rooms.user.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
