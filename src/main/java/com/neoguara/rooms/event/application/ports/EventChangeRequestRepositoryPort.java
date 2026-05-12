package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;

import java.util.List;
import java.util.Optional;

public interface EventChangeRequestRepositoryPort {
    EventRequest save(EventRequest eventRequest);
    Optional<EventRequest> findById(EventChangeRequestId id);
    List<EventRequest> findAll();
}
