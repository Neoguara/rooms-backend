package com.neoguara.rooms.event.application.dtos;

import com.neoguara.rooms.event.domain.enums.EventRequestType;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventChangeRequest(
        EventRequestType type,
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
