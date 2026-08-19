package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.enums.EventStatus;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EventJpaRepository extends JpaRepository<Event, EventId> {

    @Query("""
            SELECT e.roomId.id FROM Event e
            WHERE e.status IN :statuses AND e.startAt < :endAt AND e.endAt > :startAt
            """)
    List<UUID> findOccupiedRoomIds(
            @Param("statuses") Collection<EventStatus> statuses,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    @Query("""
            SELECT e FROM Event e
            WHERE e.roomId.id = :roomId
              AND e.status IN :statuses
              AND e.startAt < :endAt
              AND e.endAt > :startAt
            ORDER BY e.startAt
            """)
    List<Event> findOverlapping(
            @Param("roomId") UUID roomId,
            @Param("statuses") Collection<EventStatus> statuses,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    @Query("""
            SELECT e FROM Event e
            WHERE e.status IN :statuses
              AND e.startAt < :to
              AND e.endAt > :from
            ORDER BY e.startAt
            """)
    List<Event> findOccupyingBetween(
            @Param("statuses") Collection<EventStatus> statuses,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("SELECT e FROM Event e WHERE e.seriesId.id = :seriesId ORDER BY e.startAt")
    List<Event> findBySeriesId(@Param("seriesId") UUID seriesId);
}
