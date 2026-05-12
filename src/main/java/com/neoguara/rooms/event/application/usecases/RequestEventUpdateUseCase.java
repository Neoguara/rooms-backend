package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.CreateEventRequestResponse;
import com.neoguara.rooms.event.application.dtos.UpdateEventRequest;
import com.neoguara.rooms.event.application.mappers.CreateEventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventChangeRequestRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RequestEventUpdateUseCase {

    private final EventRepositoryPort eventRepository;
    private final EventChangeRequestRepositoryPort changeRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;

    public RequestEventUpdateUseCase(
            EventRepositoryPort eventRepository,
            EventChangeRequestRepositoryPort changeRequestRepository,
            EventChangeItemRepositoryPort changeItemRepository
    ) {
        this.eventRepository = eventRepository;
        this.changeRequestRepository = changeRequestRepository;
        this.changeItemRepository = changeItemRepository;
    }

    @Transactional
    public CreateEventRequestResponse execute(UUID eventId, UpdateEventRequest request) {
        var id = EventId.of(eventId);

        var event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        var changeRequest = EventRequest.update(
                id,
                UserId.of(request.userId()),
                request.justification()
        );

        var changeItem = EventChangeItem.update(
                changeRequest.getId(),
                event.getRoomId(), RoomId.of(request.roomId()),
                event.getTitle(), request.title(),
                event.getDescription(), request.description(),
                event.getStartAt(), request.startAt(),
                event.getEndAt(), request.endAt(),
                event.isAllDay(), request.isAllDay(),
                event.getRecurrenceRule(), request.recurrenceRule()
        );

        changeRequestRepository.save(changeRequest);
        changeItemRepository.save(changeItem);

        return CreateEventRequestMapper.toResponse(changeRequest);
    }
}
