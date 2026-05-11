package com.neoguara.rooms.room.application.mappers;

import com.neoguara.rooms.room.application.dtos.building.BuildingResponse;
import com.neoguara.rooms.room.application.dtos.building.CreateBuildingRequest;
import com.neoguara.rooms.room.domain.entities.Building;

public class BuildingMapper {

    private BuildingMapper() {}

    public static Building toDomain(CreateBuildingRequest request) {
        return Building.create(request.name(), request.address(), request.totalFloors());
    }

    public static BuildingResponse toResponse(Building building) {
        return new BuildingResponse(
                building.getId().id(),
                building.getName(),
                building.getAddress(),
                building.getTotalFloors(),
                building.getStatus(),
                building.getCreatedAt(),
                building.getUpdatedAt()
        );
    }
}
