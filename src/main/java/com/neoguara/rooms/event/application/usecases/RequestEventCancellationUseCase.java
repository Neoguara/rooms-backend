package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.CancelEventRequest;
import com.neoguara.rooms.event.application.dtos.CreateEventRequestResponse;
import com.neoguara.rooms.event.application.mappers.CreateEventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RequestEventCancellationUseCase {

    private final EventRepositoryPort eventRepository;
    private final EventRequestRepositoryPort eventRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;

    public RequestEventCancellationUseCase(
            EventRepositoryPort eventRepository,
            EventRequestRepositoryPort eventRequestRepository,
            EventChangeItemRepositoryPort changeItemRepository
    ) {
        this.eventRepository = eventRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.changeItemRepository = changeItemRepository;
    }

    @Transactional
    public CreateEventRequestResponse execute(UUID eventId, CancelEventRequest request) {
        var id = EventId.of(eventId);

        var event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));

        var changeRequest = EventRequest.cancelEvent(
                id,
                UserId.of(request.userId()),
                request.justification()
        );

        var changeItem = EventChangeItem.cancel(changeRequest.getId(), event);

        eventRequestRepository.save(changeRequest);
        changeItemRepository.save(changeItem);

        return CreateEventRequestMapper.toResponse(changeRequest);
    }
}
