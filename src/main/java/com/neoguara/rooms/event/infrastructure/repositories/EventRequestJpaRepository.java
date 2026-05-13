package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRequestJpaRepository extends JpaRepository<EventRequest, EventRequestId> {
}
