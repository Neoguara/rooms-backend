package com.neoguara.rooms.room.application.dtos.roomtype;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoomTypeResponse(
        UUID id,
        String name,
        String description,
        String defaultCapacity,
        String color,
        String icon,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
