package com.neoguara.rooms.user.dto;

import com.neoguara.rooms.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @Size(min = 6) String password,
        UserRole role
) {}
