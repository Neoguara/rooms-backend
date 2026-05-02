package com.neoguara.rooms.user.application.usecases;

import com.neoguara.rooms.user.application.ports.UserRepositoryPort;
import com.neoguara.rooms.user.domain.valueobjects.UserId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class DeleteUserUseCase {

    private final UserRepositoryPort repository;

    public DeleteUserUseCase(UserRepositoryPort repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        var user = repository.findById(UserId.of(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        repository.delete(user);
    }
}
