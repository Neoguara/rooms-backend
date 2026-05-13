package com.neoguara.rooms.event.application.dtos;

import java.util.UUID;

public record CancelEventRequest(
    UUID userId,
    String justification
) {}
