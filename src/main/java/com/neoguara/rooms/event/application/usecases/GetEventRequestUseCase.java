package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.mappers.EventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.enums.EventRequestStatus;
import com.neoguara.rooms.event.domain.services.EventConflict;
import com.neoguara.rooms.event.domain.services.EventRequestConflicts;
import com.neoguara.rooms.event.domain.services.LoadedOccupancy;
import com.neoguara.rooms.event.domain.services.OccupiedSlot;
import com.neoguara.rooms.event.domain.services.RoomOccupancy;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetEventRequestUseCase {

    private final EventRequestRepositoryPort repository;
    private final EventChangeItemRepositoryPort changeItemRepository;
    private final EventRepositoryPort eventRepository;

    GetEventRequestUseCase(
            EventRequestRepositoryPort repository,
            EventChangeItemRepositoryPort changeItemRepository,
            EventRepositoryPort eventRepository
    ) {
        this.repository = repository;
        this.changeItemRepository = changeItemRepository;
        this.eventRepository = eventRepository;
    }

    public List<EventRequestResponse> findAll() {
        var eventRequests = repository.findAll();

        var itemsByRequest = changeItemRepository
                .findByEventRequestIdIn(eventRequests.stream().map(EventRequest::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(EventChangeItem::getEventRequestId));

        RoomOccupancy occupancy = agendaFor(pendingItems(eventRequests, itemsByRequest));

        return eventRequests.stream()
                .map(eventRequest -> {
                    var items = itemsByRequest.getOrDefault(eventRequest.getId(), List.of());
                    return EventRequestMapper.toResponse(eventRequest, items, conflictsOf(eventRequest, items, occupancy));
                })
                .toList();
    }

    /**
     * Só grupos pendentes recebem aviso de conflito: num grupo já decidido não há o que avisar —
     * as alterações aprovadas já estão na agenda, e as rejeitadas nunca chegaram nela.
     */
    private List<EventConflict> conflictsOf(
            EventRequest eventRequest,
            List<EventChangeItem> items,
            RoomOccupancy occupancy
    ) {
        return eventRequest.getStatus() == EventRequestStatus.PENDING
                ? EventRequestConflicts.preview(items, occupancy)
                : List.of();
    }

    private List<EventChangeItem> pendingItems(
            List<EventRequest> eventRequests,
            Map<EventRequestId, List<EventChangeItem>> itemsByRequest
    ) {
        return eventRequests.stream()
                .filter(eventRequest -> eventRequest.getStatus() == EventRequestStatus.PENDING)
                .flatMap(eventRequest -> itemsByRequest.getOrDefault(eventRequest.getId(), List.of()).stream())
                .toList();
    }

    /**
     * Carrega de uma vez a faixa da agenda que cobre todos os grupos pendentes. Sem isso a listagem
     * dispararia uma consulta por alteração de cada grupo.
     */
    private RoomOccupancy agendaFor(List<EventChangeItem> items) {
        var claims = EventRequestConflicts.claims(items);
        if (claims.isEmpty()) return LoadedOccupancy.of(List.of());

        LocalDateTime from = claims.stream().map(OccupiedSlot::startAt).min(Comparator.naturalOrder()).orElseThrow();
        LocalDateTime to = claims.stream().map(OccupiedSlot::endAt).max(Comparator.naturalOrder()).orElseThrow();

        List<Event> agenda = eventRepository.findOccupyingBetween(from, to);
        return LoadedOccupancy.of(agenda);
    }
}
