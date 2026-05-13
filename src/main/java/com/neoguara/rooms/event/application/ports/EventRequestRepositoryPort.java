package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;

import java.util.List;
import java.util.Optional;

public interface EventRequestRepositoryPort {
    EventRequest save(EventRequest eventRequest);
    Optional<EventRequest> findById(EventRequestId id);
    List<EventRequest> findAll();
}
