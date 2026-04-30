package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.EventChangeRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventChangeRequestRepositoryPort extends JpaRepository<EventChangeRequest, EventChangeRequestId> {
}
