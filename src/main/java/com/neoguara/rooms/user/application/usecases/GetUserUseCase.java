package com.neoguara.rooms.user.application.usecases;

import com.neoguara.rooms.user.application.dtos.UserResponse;
import com.neoguara.rooms.user.application.mappers.UserMapper;
import com.neoguara.rooms.user.application.ports.UserRepositoryPort;
import com.neoguara.rooms.user.domain.valueobjects.UserId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class GetUserUseCase {

    private final UserRepositoryPort repository;

    public GetUserUseCase(UserRepositoryPort repository) {
        this.repository = repository;
    }

    public UserResponse findById(UUID id) {
        return repository.findById(UserId.of(id))
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public List<UserResponse> findAll() {
        return repository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}
