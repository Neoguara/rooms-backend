package com.neoguara.rooms.room.application.usecases.resource;

import com.neoguara.rooms.room.application.dtos.resource.ResourceResponse;
import com.neoguara.rooms.room.application.dtos.resource.UpdateResourceStatusRequest;
import com.neoguara.rooms.room.application.mappers.ResourceMapper;
import com.neoguara.rooms.room.application.ports.ResourceRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Resource;
import com.neoguara.rooms.room.domain.enums.ResourceStatus;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateResourceStatusUseCase {

    private final ResourceRepositoryPort repository;

    public UpdateResourceStatusUseCase(ResourceRepositoryPort repository) {
        this.repository = repository;
    }

    public ResourceResponse execute(UUID id, UpdateResourceStatusRequest.Status status) {
        Resource resource = repository.findById(ResourceId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id));

        switch (status) {
            case ACTIVE -> {
                if (resource.getStatus() == ResourceStatus.ARCHIVED) {
                    resource.restore();
                } else {
                    resource.activate();
                }
            }
            case INACTIVE -> resource.deactivate();
            case ARCHIVED -> resource.archive();
        }

        return ResourceMapper.toResponse(repository.save(resource));
    }
}
