package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventChangeItemJpaRepository extends JpaRepository<EventChangeItem, EventChangeItemId> {
    Optional<EventChangeItem> findByEventChangeRequestId(EventRequestId eventRequestId);
}
