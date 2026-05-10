package com.neoguara.rooms.room.application.dtos.resource;

public record UpdateResourceRequest(
        String name,
        String description,
        String icon
) {}
