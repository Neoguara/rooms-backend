package com.neoguara.rooms.room.application.usecases.building;

import com.neoguara.rooms.room.application.dtos.building.BuildingResponse;
import com.neoguara.rooms.room.application.dtos.building.UpdateBuildingRequest;
import com.neoguara.rooms.room.application.mappers.building.BuildingMapper;
import com.neoguara.rooms.room.application.ports.BuildingRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Building;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateBuildingUseCase {

    private final BuildingRepositoryPort repository;

    public UpdateBuildingUseCase(BuildingRepositoryPort repository) {
        this.repository = repository;
    }

    public BuildingResponse execute(UUID id, UpdateBuildingRequest request) {
        Building building = repository.findById(BuildingId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("Building", id));

        building.update(request.name(), request.address(), request.totalFloors());

        return BuildingMapper.toResponse(repository.save(building));
    }
}
