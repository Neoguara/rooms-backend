package com.neoguara.rooms.room.application.dtos.room;

import com.neoguara.rooms.room.domain.enums.RoomStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoomResponse(
        UUID id,
        String name,
        String code,
        String type,
        UUID roomTypeId,
        UUID buildingId,
        int floor,
        int capacity,
        RoomStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
