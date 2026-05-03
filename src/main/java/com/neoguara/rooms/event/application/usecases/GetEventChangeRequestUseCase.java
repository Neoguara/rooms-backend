package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.EventChangeRequestResponse;
import com.neoguara.rooms.event.application.mappers.EventChangeRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeRequestRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetEventChangeRequestUseCase {

    private final EventChangeRequestRepositoryPort repository;

    GetEventChangeRequestUseCase(EventChangeRequestRepositoryPort repository) {
        this.repository = repository;
    }

    public List<EventChangeRequestResponse> findAll() {
        return repository.findAll().stream().map(EventChangeRequestMapper::toResponse).toList();
    }
}
