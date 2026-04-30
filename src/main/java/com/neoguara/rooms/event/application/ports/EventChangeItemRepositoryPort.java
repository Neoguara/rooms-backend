package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventChangeItemRepositoryPort extends JpaRepository<EventChangeItem, EventChangeItemId> {
}
