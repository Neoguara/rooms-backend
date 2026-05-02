package com.neoguara.rooms.event.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventResponse(
    UUID id,
    UUID roomId,
    String title,
    String description,
    LocalDateTime startAt,
    LocalDateTime endAt,
    Boolean isAllDay,
    String recurrenceRule,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt
    ) {
}
