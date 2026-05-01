package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.valueobjects.EventId;

import java.util.List;
import java.util.Optional;

public interface EventRepositoryPort {
    List<Event> findAll();
    Optional<Event> findById(EventId id);
    Event save(Event event);
}
