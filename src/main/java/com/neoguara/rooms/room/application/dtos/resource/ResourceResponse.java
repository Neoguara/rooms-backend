package com.neoguara.rooms.room.application.dtos.resource;

import com.neoguara.rooms.room.domain.enums.ResourceStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResourceResponse(
        UUID id,
        String name,
        String description,
        String icon,
        ResourceStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
