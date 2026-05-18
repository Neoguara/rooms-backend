package com.neoguara.rooms.room.application.dtos.room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoomAvailabilityFilter(
        LocalDateTime startAt,
        LocalDateTime endAt,
        UUID roomTypeId,
        List<UUID> resourceIds,
        Integer minCapacity
) {}
