package com.neoguara.rooms.room.application.dtos.room;

import java.util.List;
import java.util.UUID;

public record CreateRoomRequest(
        String name,
        String code,
        UUID roomTypeId,
        UUID buildingId,
        int floor,
        int capacity,
        List<UUID> resourceIds
) {}
