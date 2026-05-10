package com.neoguara.rooms.room.application.dtos.room;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoomResponse(
        UUID id,
        String name,
        String code,
        String type,
        String building,
        String resources,
        int floor,
        int capacity,
        boolean isActive,
        LocalDateTime createdAt
) {}
