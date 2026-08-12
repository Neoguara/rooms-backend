package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EventRequestRepositoryImpl implements EventRequestRepositoryPort {

    private final EventRequestJpaRepository repository;

    public EventRequestRepositoryImpl(EventRequestJpaRepository repository) {this.repository = repository;}

    @Override
    public EventRequest save(EventRequest eventRequest) {
        return repository.save(eventRequest);
    }

    @Override
    public Optional<EventRequest> findById(EventRequestId id) {
        return repository.findById(id);
    }

    @Override
    public List<EventRequest> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean hasReversal(EventRequestId eventRequestId) {
        return repository.existsByReversalOf(eventRequestId.id());
    }
}
