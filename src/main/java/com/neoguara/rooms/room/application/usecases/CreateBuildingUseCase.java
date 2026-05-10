package com.neoguara.rooms.room.application.usecases;

import com.neoguara.rooms.room.application.dtos.BuildingResponse;
import com.neoguara.rooms.room.application.dtos.CreateBuildingRequest;
import com.neoguara.rooms.room.application.mappers.BuildingMapper;
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
