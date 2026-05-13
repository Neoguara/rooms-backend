package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;

import java.util.Optional;

public interface EventChangeItemRepositoryPort {
    EventChangeItem save (EventChangeItem eventChangeItem);
    Optional<EventChangeItem> findByEventChangeRequestId(EventRequestId eventRequestId);
}
