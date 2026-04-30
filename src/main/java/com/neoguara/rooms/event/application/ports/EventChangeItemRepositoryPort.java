package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventChangeItemRepositoryPort extends JpaRepository<EventChangeItem, EventChangeItemId> {
    Optional<EventChangeItem> findByEventChangeRequestId(EventChangeRequestId eventChangeRequestId);
}
