package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;

import java.util.Optional;

public interface EventChangeItemRepositoryPort {
    EventChangeItem save (EventChangeItem eventChangeItem);
    Optional<EventChangeItem> findByEventChangeRequestId(EventChangeRequestId eventChangeRequestId);
}
