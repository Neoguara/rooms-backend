package com.neoguara.rooms.room.application.usecases;

import com.neoguara.rooms.room.application.dtos.BuildingResponse;
import com.neoguara.rooms.room.application.mappers.BuildingMapper;
import com.neoguara.rooms.room.application.ports.BuildingRepositoryPort;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetBuildingUseCase {

    private final BuildingRepositoryPort repository;

    public GetBuildingUseCase(BuildingRepositoryPort repository) {
        this.repository = repository;
    }

    public BuildingResponse findById(UUID id) {
        return repository.findById(BuildingId.of(id))
                .map(BuildingMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Building", id));
    }

    public List<BuildingResponse> findAll() {
        return repository.findAll().stream()
                .map(BuildingMapper::toResponse)
                .toList();
    }
}
