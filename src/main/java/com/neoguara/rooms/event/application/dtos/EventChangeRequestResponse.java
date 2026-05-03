package com.neoguara.rooms.event.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventChangeRequestResponse(
    UUID id,
    UUID eventId,
    UUID createdBy,
    String status,
    String type,
    String justification,
    LocalDateTime createdAt,
    EventChangeItemResponse changeItem
) {}
