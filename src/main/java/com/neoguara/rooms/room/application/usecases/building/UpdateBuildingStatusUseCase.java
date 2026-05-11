package com.neoguara.rooms.room.application.usecases.building;

import com.neoguara.rooms.room.application.dtos.building.BuildingResponse;
import com.neoguara.rooms.room.application.mappers.BuildingMapper;
import com.neoguara.rooms.room.application.ports.BuildingRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Building;
import com.neoguara.rooms.room.domain.enums.BuildingStatus;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateBuildingStatusUseCase {

    private final BuildingRepositoryPort repository;

    public UpdateBuildingStatusUseCase(BuildingRepositoryPort repository) {
        this.repository = repository;
    }

    public BuildingResponse execute(UUID id, BuildingStatus status) {
        Building building = repository.findById(BuildingId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("Building", id));

        switch (status) {
            case ACTIVE -> {
                if (building.getStatus() == BuildingStatus.ARCHIVED) {
                    building.restore();
                } else {
                    building.activate();
                }
            }
            case INACTIVE -> building.deactivate();
            case ARCHIVED -> building.archive();
        }

        return BuildingMapper.toResponse(repository.save(building));
    }
}
