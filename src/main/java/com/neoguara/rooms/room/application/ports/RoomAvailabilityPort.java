package com.neoguara.rooms.room.application.ports;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface RoomAvailabilityPort {
    Set<UUID> findOccupiedRoomIds(LocalDateTime startAt, LocalDateTime endAt);
}
