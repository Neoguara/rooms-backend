package com.neoguara.rooms.room.application.dtos.room;

import java.util.UUID;

public record CreateRoomRequest(
        String name,
        String code,
        String type,
        UUID roomTypeId,
        UUID buildingId,
        int floor,
        int capacity
) {}
