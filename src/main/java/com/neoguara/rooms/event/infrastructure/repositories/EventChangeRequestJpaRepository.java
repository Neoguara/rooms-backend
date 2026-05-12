package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventChangeRequestJpaRepository extends JpaRepository<EventRequest, EventChangeRequestId> {
}
