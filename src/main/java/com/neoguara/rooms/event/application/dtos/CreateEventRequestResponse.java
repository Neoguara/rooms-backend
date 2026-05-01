package com.neoguara.rooms.event.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateEventRequestResponse (
    UUID id,
    UUID createdBy,
    String status,
    String type,
    String justification,
    LocalDateTime createdAt
) {}
