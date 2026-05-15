package com.neoguara.rooms.user.application.usecases;

import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import com.neoguara.rooms.user.application.dtos.UpdateUserStatusRequest;
import com.neoguara.rooms.user.application.dtos.UserResponse;
import com.neoguara.rooms.user.application.mappers.UserMapper;
import com.neoguara.rooms.user.application.ports.UserRepositoryPort;
import com.neoguara.rooms.user.domain.entities.User;
import com.neoguara.rooms.user.domain.valueobjects.UserId;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateUserStatusUseCase {

    private final UserRepositoryPort repository;

    public UpdateUserStatusUseCase(UserRepositoryPort repository) {
        this.repository = repository;
    }

    public UserResponse execute(UUID id, UpdateUserStatusRequest.Status status) {
        User user = repository.findById(UserId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        switch (status) {
            case ACTIVE -> user.activate();
            case INACTIVE -> user.deactivate();
        }

        return UserMapper.toResponse(repository.save(user));
    }
}
