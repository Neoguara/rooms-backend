package com.neoguara.rooms.event.application.dtos;

import java.util.UUID;

public record DeleteEventRequest(
    UUID userId,
    String justification
) {}
