package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.enums.EventStatus;
import com.neoguara.rooms.event.domain.valueobjects.EventId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepositoryPort {
    List<Event> findAll();
    Optional<Event> findById(EventId id);
    Event save(Event event);
    List<UUID> findOccupiedRoomIds(EventStatus status, LocalDateTime startAt, LocalDateTime endAt);
}
