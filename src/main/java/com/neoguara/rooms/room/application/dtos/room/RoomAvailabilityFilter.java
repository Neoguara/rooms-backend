package com.neoguara.rooms.room.application.dtos.room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoomAvailabilityFilter(
        LocalDateTime startAt,
        LocalDateTime endAt,
        List<UUID> roomTypeIds,
        List<UUID> resourceIds,
        List<UUID> buildingIds,
        Integer minCapacity,
        String search
) {}
