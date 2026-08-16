package com.neoguara.rooms.event.infrastructure.adapters;

import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.services.RoomOccupancy;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RoomOccupancyAdapter implements RoomOccupancy {

    private final EventRepositoryPort eventRepository;

    public RoomOccupancyAdapter(EventRepositoryPort eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> occupying(RoomId roomId, LocalDateTime startAt, LocalDateTime endAt) {
        return eventRepository.findOverlapping(roomId, startAt, endAt);
    }
}
