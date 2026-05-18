package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.enums.EventStatus;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventJpaRepository extends JpaRepository<Event, EventId> {

    @Query("SELECT e.roomId.id FROM Event e WHERE e.status = :status AND e.startAt < :endAt AND e.endAt > :startAt")
    List<UUID> findOccupiedRoomIds(
            @Param("status") EventStatus status,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );
}
