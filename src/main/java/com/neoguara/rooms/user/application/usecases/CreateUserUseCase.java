package com.neoguara.rooms.user.application.usecases;

import com.neoguara.rooms.user.application.dtos.CreateUserRequest;
import com.neoguara.rooms.user.application.dtos.UserResponse;
import com.neoguara.rooms.user.application.mappers.UserMapper;
import com.neoguara.rooms.user.application.ports.UserRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CreateUserUseCase {

    private final UserRepositoryPort repository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepositoryPort repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse execute(CreateUserRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        var user = UserMapper.toDomain(request, passwordEncoder.encode(request.password()));
        return UserMapper.toResponse(repository.save(user));
    }
}
