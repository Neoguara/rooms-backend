package com.neoguara.rooms.room.application.dtos.resource;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResourceResponse(
        UUID id,
        String name,
        String description,
        String icon,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
