package com.neoguara.rooms.room.application.dtos.resource;

public record CreateResourceRequest(
        String name,
        String description,
        String icon
) {}
