package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EventRequestJpaRepository extends JpaRepository<EventRequest, EventRequestId> {

    @Query("SELECT COUNT(r) > 0 FROM EventRequest r WHERE r.reversalOf.id = :id")
    boolean existsByReversalOf(@Param("id") UUID id);
}
