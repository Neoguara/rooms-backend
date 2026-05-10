package com.neoguara.rooms.room.application.usecases.building;

import com.neoguara.rooms.room.application.ports.BuildingRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Building;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteBuildingUseCase {

    private final BuildingRepositoryPort repository;

    public DeleteBuildingUseCase(BuildingRepositoryPort repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        Building building = repository.findById(BuildingId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("Building", id));
        repository.delete(building);
    }
}
