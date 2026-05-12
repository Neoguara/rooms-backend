package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.application.ports.EventChangeRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EventChangeRequestRepositoryImpl implements EventChangeRequestRepositoryPort {

    private final EventChangeRequestJpaRepository repository;

    public EventChangeRequestRepositoryImpl(EventChangeRequestJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public EventRequest save(EventRequest eventRequest) {
        return repository.save(eventRequest);
    }

    @Override
    public Optional<EventRequest> findById(EventChangeRequestId id) {
        return repository.findById(id);
    }

    @Override
    public List<EventRequest> findAll() {
        return repository.findAll();
    }
}
