package com.neoguara.rooms.event.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateEventRequest(
        String title,
        String description,
        LocalDateTime startAt,
        LocalDateTime endAt,
        boolean isAllDay,
        String recurrenceRule,
        String justification,
        UUID userId
) {}
