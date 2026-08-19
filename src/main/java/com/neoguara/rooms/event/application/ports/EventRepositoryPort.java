package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import com.neoguara.rooms.event.domain.valueobjects.SeriesId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepositoryPort {
    List<Event> findAll();
    Optional<Event> findById(EventId id);
    Event save(Event event);
    List<UUID> findOccupiedRoomIds(LocalDateTime startAt, LocalDateTime endAt);

    /**
     * Eventos que seguram {@code roomId} em algum ponto do intervalo. O próprio evento consultado
     * pode vir na lista: descartá-lo é decisão de domínio, não da consulta.
     */
    List<Event> findOverlapping(RoomId roomId, LocalDateTime startAt, LocalDateTime endAt);

    /**
     * Eventos que seguram <em>qualquer</em> sala dentro do intervalo. Serve para carregar a agenda
     * de uma vez quando muitas verificações seguidas viriam a seguir.
     */
    List<Event> findOccupyingBetween(LocalDateTime from, LocalDateTime to);

    /** Ocorrências de uma mesma recorrência, em ordem cronológica. */
    List<Event> findBySeriesId(SeriesId seriesId);
}
