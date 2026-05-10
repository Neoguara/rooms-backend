package com.neoguara.rooms.room.application.dtos.roomtype;

public record CreateRoomTypeRequest(
        String name,
        String description,
        String defaultCapacity,
        String color,
        String icon
) {}
