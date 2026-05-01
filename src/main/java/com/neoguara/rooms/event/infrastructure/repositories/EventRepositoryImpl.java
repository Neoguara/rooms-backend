package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Event save(Event event) {
        return jpaRepository.save(event);
    }

}
