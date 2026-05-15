package com.neoguara.rooms.room.application.usecases.resource;

import com.neoguara.rooms.room.application.ports.ResourceRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Resource;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteResourceUseCase {

    private final ResourceRepositoryPort repository;

    public DeleteResourceUseCase(ResourceRepositoryPort repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        Resource resource = repository.findById(ResourceId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id));
        resource.delete();
        repository.save(resource);
    }
}
