package com.neoguara.rooms.room.application.dtos.roomtype;

public record UpdateRoomTypeRequest(
        String name,
        String description,
        String defaultCapacity,
        String color,
        String icon
) {}
