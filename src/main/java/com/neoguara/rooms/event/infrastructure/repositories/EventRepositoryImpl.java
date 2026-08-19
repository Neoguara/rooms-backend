package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.enums.EventStatus;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import com.neoguara.rooms.event.domain.valueobjects.SeriesId;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EventRepositoryImpl implements EventRepositoryPort {

    private final EventJpaRepository jpaRepository;

    public EventRepositoryImpl(EventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Event> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<Event> findById(EventId id) {
        return jpaRepository.findById(id);
    }

    /**
     * Descarrega na hora em vez de deixar para o commit. Um mesmo grupo de alterações é aplicado
     * evento a evento dentro de uma transação, e a checagem de conflito de cada um precisa enxergar
     * os anteriores — sem o flush, duas alterações sobrepostas do mesmo grupo passariam batido.
     */
    @Override
    public Event save(Event event) {
        return jpaRepository.saveAndFlush(event);
    }

    @Override
    public List<UUID> findOccupiedRoomIds(LocalDateTime startAt, LocalDateTime endAt) {
        return jpaRepository.findOccupiedRoomIds(EventStatus.occupying(), startAt, endAt);
    }

    @Override
    public List<Event> findOverlapping(RoomId roomId, LocalDateTime startAt, LocalDateTime endAt) {
        return jpaRepository.findOverlapping(roomId.id(), EventStatus.occupying(), startAt, endAt);
    }

    @Override
    public List<Event> findOccupyingBetween(LocalDateTime from, LocalDateTime to) {
        return jpaRepository.findOccupyingBetween(EventStatus.occupying(), from, to);
    }

    @Override
    public List<Event> findBySeriesId(SeriesId seriesId) {
        return jpaRepository.findBySeriesId(seriesId.id());
    }

}
