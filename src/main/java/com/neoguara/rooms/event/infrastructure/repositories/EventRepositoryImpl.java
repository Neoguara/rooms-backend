package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import org.springframework.stereotype.Repository;

import java.util.List;

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

}
