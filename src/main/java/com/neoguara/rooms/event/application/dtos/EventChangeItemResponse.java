package com.neoguara.rooms.event.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventChangeItemResponse(
    UUID id,
    UUID oldRoomId,
    UUID newRoomId,
    String oldTitle,
    String newTitle,
    String oldDescription,
    String newDescription,
    LocalDateTime oldStartAt,
    LocalDateTime newStartAt,
    LocalDateTime oldEndAt,
    LocalDateTime newEndAt,
    Boolean oldIsAllDay,
    Boolean newIsAllDay,
    String oldRecurrenceRule,
    String newRecurrenceRule
) {}
