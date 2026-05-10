package com.neoguara.rooms.room.application.dtos.building;

import com.neoguara.rooms.room.domain.enums.BuildingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record BuildingResponse(
        UUID id,
        String name,
        String address,
        Integer totalFloors,
        BuildingStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
