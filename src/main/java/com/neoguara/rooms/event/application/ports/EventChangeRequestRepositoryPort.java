package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.EventChangeRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;

import java.util.Optional;

public interface EventChangeRequestRepositoryPort {
    EventChangeRequest save (EventChangeRequest eventChangeRequest);
    Optional<EventChangeRequest> findById(EventChangeRequestId id);
}
