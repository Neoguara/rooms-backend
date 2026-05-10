package com.neoguara.rooms.room.application.usecases.building;

import com.neoguara.rooms.room.application.dtos.building.BuildingResponse;
import com.neoguara.rooms.room.application.dtos.building.CreateBuildingRequest;
import com.neoguara.rooms.room.application.mappers.building.BuildingMapper;
import com.neoguara.rooms.room.application.ports.BuildingRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Building;
import org.springframework.stereotype.Service;

@Service
public class CreateBuildingUseCase {

    private final BuildingRepositoryPort repository;

    public CreateBuildingUseCase(BuildingRepositoryPort repository) {
        this.repository = repository;
    }

    public BuildingResponse execute(CreateBuildingRequest request) {
        Building building = BuildingMapper.toDomain(request);
        Building saved = repository.save(building);
        return BuildingMapper.toResponse(saved);
    }
}
