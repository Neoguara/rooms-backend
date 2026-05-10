package com.neoguara.rooms.user.application.usecases;

import com.neoguara.rooms.user.application.dtos.CreateUserRequest;
import com.neoguara.rooms.user.application.dtos.UserResponse;
import com.neoguara.rooms.user.application.mappers.UserMapper;
import com.neoguara.rooms.user.application.ports.UserRepositoryPort;
import com.neoguara.rooms.shared.domain.exceptions.ConflictException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            throw new ConflictException("Email already in use");
        }
        var user = UserMapper.toDomain(request, passwordEncoder.encode(request.password()));
        return UserMapper.toResponse(repository.save(user));
    }
}