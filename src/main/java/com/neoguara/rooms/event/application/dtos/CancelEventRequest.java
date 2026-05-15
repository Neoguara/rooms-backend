package com.neoguara.rooms.event.application.dtos;

import java.util.UUID;

public record CancelEventRequest(
    UUID eventId,
    UUID userId,
    String justification
) {}
