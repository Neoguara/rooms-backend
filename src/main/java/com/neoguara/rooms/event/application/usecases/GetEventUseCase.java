package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.EventResponse;
import com.neoguara.rooms.event.application.mappers.EventMapper;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetEventUseCase {

    private final EventRepositoryPort repository;

    GetEventUseCase(EventRepositoryPort repository) {
        this.repository = repository;
    }

    public List<EventResponse> findAll() {
        return repository.findAll().stream().map(EventMapper::toResponse).toList();
    }
}
