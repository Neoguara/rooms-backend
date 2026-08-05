package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.mappers.EventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        var eventRequests = repository.findAll();

        var itemsByRequest = changeItemRepository
                .findByEventRequestIdIn(eventRequests.stream().map(EventRequest::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(EventChangeItem::getEventRequestId));

        return eventRequests.stream()
                .map(eventRequest -> EventRequestMapper.toResponse(
                        eventRequest,
                        itemsByRequest.getOrDefault(eventRequest.getId(), List.of())
                ))
                .toList();
    }
}
