package com.neoguara.rooms.room.application.dtos.room;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.neoguara.rooms.room.application.dtos.building.BuildingResponse;
import com.neoguara.rooms.room.application.dtos.resource.ResourceResponse;
import com.neoguara.rooms.room.application.dtos.roomtype.RoomTypeResponse;
import com.neoguara.rooms.room.domain.enums.RoomStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoomDetailResponse(
        UUID id,
        String name,
        String code,
        UUID roomTypeId,
        UUID buildingId,
        int floor,
        int capacity,
        RoomStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        BuildingResponse building,
        RoomTypeResponse roomType,
        List<ResourceResponse> resources
) {}
