package com.neoguara.rooms.room.application.dtos.room;

public record CreateRoomRequest(
        String name,
        String code,
        String type,
        String building,
        String resources,
        int floor,
        int capacity
) {}
