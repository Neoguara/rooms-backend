package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.CreateEventRequestResponse;
import com.neoguara.rooms.event.application.dtos.DeleteEventRequest;
import com.neoguara.rooms.event.application.mappers.CreateEventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RequestEventDeletionUseCase {

    private final EventRepositoryPort eventRepository;
    private final EventRequestRepositoryPort changeRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;

    public RequestEventDeletionUseCase(
            EventRepositoryPort eventRepository,
            EventRequestRepositoryPort changeRequestRepository,
            EventChangeItemRepositoryPort changeItemRepository
    ) {
        this.eventRepository = eventRepository;
        this.changeRequestRepository = changeRequestRepository;
        this.changeItemRepository = changeItemRepository;
    }

    @Transactional
    public CreateEventRequestResponse execute(UUID eventId, DeleteEventRequest request) {
        var id = EventId.of(eventId);

        var event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        var changeRequest = EventRequest.delete(
                id,
                UserId.of(request.userId()),
                request.justification()
        );

        var changeItem = EventChangeItem.delete(changeRequest.getId(), event);

        changeRequestRepository.save(changeRequest);
        changeItemRepository.save(changeItem);

        return CreateEventRequestMapper.toResponse(changeRequest);
    }
}
