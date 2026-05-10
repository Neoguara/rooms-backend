package com.neoguara.rooms.user.application.usecases;

import com.neoguara.rooms.user.application.dtos.UserResponse;
import com.neoguara.rooms.user.application.mappers.UserMapper;
import com.neoguara.rooms.user.application.ports.UserRepositoryPort;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import com.neoguara.rooms.user.domain.valueobjects.UserId;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public List<UserResponse> findAll() {
        return repository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}
