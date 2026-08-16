package com.neoguara.rooms.event.infrastructure.adapters;

import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.room.application.ports.RoomAvailabilityPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class EventAvailabilityAdapter implements RoomAvailabilityPort {

    private final EventRepositoryPort eventRepository;

    public EventAvailabilityAdapter(EventRepositoryPort eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Set<UUID> findOccupiedRoomIds(LocalDateTime startAt, LocalDateTime endAt) {
        return new HashSet<>(eventRepository.findOccupiedRoomIds(startAt, endAt));
    }
}
