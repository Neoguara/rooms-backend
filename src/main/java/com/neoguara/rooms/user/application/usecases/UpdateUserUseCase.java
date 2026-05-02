package com.neoguara.rooms.user.application.usecases;

import com.neoguara.rooms.user.application.dtos.UpdateUserRequest;
import com.neoguara.rooms.user.application.dtos.UserResponse;
import com.neoguara.rooms.user.application.mappers.UserMapper;
import com.neoguara.rooms.user.application.ports.UserRepositoryPort;
import com.neoguara.rooms.user.domain.valueobjects.UserId;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UpdateUserUseCase {

    private final UserRepositoryPort repository;
    private final PasswordEncoder passwordEncoder;

    public UpdateUserUseCase(UserRepositoryPort repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse execute(UUID id, UpdateUserRequest request) {
        var user = repository.findById(UserId.of(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var newPassword = (request.password() != null && !request.password().isBlank())
                ? passwordEncoder.encode(request.password())
                : user.getPassword();

        var newRole = request.role() != null ? request.role() : user.getRole();

        user.update(request.name(), request.email(), newPassword, newRole);
        return UserMapper.toResponse(repository.save(user));
    }
}
