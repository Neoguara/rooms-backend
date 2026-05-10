package com.neoguara.rooms.room.application.usecases.resource;

import com.neoguara.rooms.room.application.dtos.resource.CreateResourceRequest;
import com.neoguara.rooms.room.application.dtos.resource.ResourceResponse;
import com.neoguara.rooms.room.application.mappers.resource.ResourceMapper;
import com.neoguara.rooms.room.application.ports.ResourceRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Resource;
import org.springframework.stereotype.Service;

@Service
public class CreateResourceUseCase {

    private final ResourceRepositoryPort repository;

    public CreateResourceUseCase(ResourceRepositoryPort repository) {
        this.repository = repository;
    }

    public ResourceResponse execute(CreateResourceRequest request) {
        Resource resource = ResourceMapper.toDomain(request);
        Resource saved = repository.save(resource);
        return ResourceMapper.toResponse(saved);
    }
}
