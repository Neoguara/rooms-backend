package com.neoguara.rooms.room.application.usecases.resource;

import com.neoguara.rooms.room.application.dtos.resource.ResourceResponse;
import com.neoguara.rooms.room.application.dtos.resource.UpdateResourceRequest;
import com.neoguara.rooms.room.application.mappers.ResourceMapper;
import com.neoguara.rooms.room.application.ports.ResourceRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Resource;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateResourceUseCase {

    private final ResourceRepositoryPort repository;

    public UpdateResourceUseCase(ResourceRepositoryPort repository) {
        this.repository = repository;
    }

    public ResourceResponse execute(UUID id, UpdateResourceRequest request) {
        Resource resource = repository.findById(ResourceId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id));

        resource.update(request.name(), request.description(), request.icon());

        return ResourceMapper.toResponse(repository.save(resource));
    }
}
