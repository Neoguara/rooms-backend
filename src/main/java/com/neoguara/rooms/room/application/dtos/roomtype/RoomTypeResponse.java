package com.neoguara.rooms.room.application.dtos.roomtype;

import com.neoguara.rooms.room.domain.enums.RoomTypeStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoomTypeResponse(
        UUID id,
        String name,
        String description,
        String defaultCapacity,
        String color,
        String icon,
        RoomTypeStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
