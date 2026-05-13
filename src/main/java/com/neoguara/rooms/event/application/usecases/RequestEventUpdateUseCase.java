package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.CreateEventRequestResponse;
import com.neoguara.rooms.event.application.dtos.UpdateEventRequest;
import com.neoguara.rooms.event.application.mappers.CreateEventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.EventSnapshot;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RequestEventUpdateUseCase {

    private final EventRepositoryPort eventRepository;
    private final EventRequestRepositoryPort eventRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;

    public RequestEventUpdateUseCase(
            EventRepositoryPort eventRepository,
            EventRequestRepositoryPort eventRequestRepository,
            EventChangeItemRepositoryPort changeItemRepository
    ) {
        this.eventRepository = eventRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.changeItemRepository = changeItemRepository;
    }

    @Transactional
    public CreateEventRequestResponse execute(UUID eventId, UpdateEventRequest request) {
        var id = EventId.of(eventId);

        var event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        var changeRequest = EventRequest.updateEvent(
                id,
                UserId.of(request.userId()),
                request.justification()
        );

        var after = EventSnapshot.of(
                RoomId.of(request.roomId()),
                request.title(), request.description(),
                request.startAt(), request.endAt(),
                request.isAllDay(), request.recurrenceRule()
        );
        var changeItem = EventChangeItem.update(changeRequest.getId(), event, after);

        eventRequestRepository.save(changeRequest);
        changeItemRepository.save(changeItem);

        return CreateEventRequestMapper.toResponse(changeRequest);
    }
}
