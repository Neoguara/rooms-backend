package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.EventResponse;
import com.neoguara.rooms.event.application.mappers.EventMapper;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.valueobjects.SeriesId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetEventUseCase {

    private final EventRepositoryPort repository;

    GetEventUseCase(EventRepositoryPort repository) {
        this.repository = repository;
    }

    public List<EventResponse> findAll() {
        return repository.findAll().stream().map(EventMapper::toResponse).toList();
    }

    /** Ocorrências de uma recorrência, em ordem cronológica. */
    public List<EventResponse> findBySeries(UUID seriesId) {
        return repository.findBySeriesId(SeriesId.of(seriesId)).stream().map(EventMapper::toResponse).toList();
    }
}
