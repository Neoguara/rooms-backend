package com.neoguara.rooms.user.application.mappers;

import com.neoguara.rooms.user.application.dtos.CreateUserRequest;
import com.neoguara.rooms.user.application.dtos.UserResponse;
import com.neoguara.rooms.user.domain.entities.User;

public class UserMapper {

    private UserMapper() {}

    public static User toDomain(CreateUserRequest request, String encodedPassword) {
        return new User(request.name(), request.email(), encodedPassword, request.role());
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId().id(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
