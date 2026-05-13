package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.mappers.EventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetEventRequestUseCase {

    private final EventRequestRepositoryPort repository;
    private final EventChangeItemRepositoryPort changeItemRepository;

    GetEventRequestUseCase(
            EventRequestRepositoryPort repository,
            EventChangeItemRepositoryPort changeItemRepository
    ) {
        this.repository = repository;
        this.changeItemRepository = changeItemRepository;
    }

    public List<EventRequestResponse> findAll() {
        return repository.findAll().stream()
                .map(eventRequest -> {
                    var changeItem = changeItemRepository
                            .findByEventRequestId(eventRequest.getId())
                            .orElse(null);
                    return EventRequestMapper.toResponse(eventRequest, changeItem);
                })
                .toList();
    }
}
