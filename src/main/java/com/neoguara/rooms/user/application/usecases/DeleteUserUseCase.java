package com.neoguara.rooms.user.application.usecases;

import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import com.neoguara.rooms.user.application.ports.UserRepositoryPort;
import com.neoguara.rooms.user.domain.valueobjects.UserId;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteUserUseCase {

    private final UserRepositoryPort repository;

    public DeleteUserUseCase(UserRepositoryPort repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        var user = repository.findById(UserId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.delete();
        repository.save(user);
    }
}
