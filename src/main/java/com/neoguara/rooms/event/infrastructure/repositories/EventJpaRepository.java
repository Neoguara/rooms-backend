package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<Event, EventId> {
}
