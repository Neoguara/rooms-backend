package com.neoguara.rooms.room.application.usecases.resource;

import com.neoguara.rooms.room.application.dtos.resource.ResourceResponse;
import com.neoguara.rooms.room.application.mappers.resource.ResourceMapper;
import com.neoguara.rooms.room.application.ports.ResourceRepositoryPort;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetResourceUseCase {

    private final ResourceRepositoryPort repository;

    public GetResourceUseCase(ResourceRepositoryPort repository) {
        this.repository = repository;
    }

    public ResourceResponse findById(UUID id) {
        return repository.findById(ResourceId.of(id))
                .map(ResourceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id));
    }

    public List<ResourceResponse> findAll() {
        return repository.findAll().stream()
                .map(ResourceMapper::toResponse)
                .toList();
    }
}
