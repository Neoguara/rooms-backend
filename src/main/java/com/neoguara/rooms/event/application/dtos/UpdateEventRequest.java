package com.neoguara.rooms.event.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateEventRequest(
        UUID eventId,
        String title,
        String description,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Boolean isAllDay,
        String recurrenceRule,
        String justification,
        UUID userId,
        UUID roomId
) {}
