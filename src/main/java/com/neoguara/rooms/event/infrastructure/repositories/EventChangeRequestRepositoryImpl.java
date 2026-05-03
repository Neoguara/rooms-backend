package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.application.ports.EventChangeRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventChangeRequest;
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
    public EventChangeRequest save(EventChangeRequest eventChangeRequest) {
        return repository.save(eventChangeRequest);
    }

    @Override
    public Optional<EventChangeRequest> findById(EventChangeRequestId id) {
        return repository.findById(id);
    }

    @Override
    public List<EventChangeRequest> findAll() {
        return repository.findAll();
    }
}
