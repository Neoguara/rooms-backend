package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.CreateEventRequest;
import com.neoguara.rooms.event.application.dtos.EventChangeRequestResponse;
import com.neoguara.rooms.event.application.mappers.EventChangeRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventChangeRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventChangeRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RequestEventCreationUseCase {

    private final EventChangeRequestRepositoryPort changeRequestRepo;
    private final EventChangeItemRepositoryPort changeItemRepo;

    RequestEventCreationUseCase(EventChangeRequestRepositoryPort changeRequestRepo,
                                EventChangeItemRepositoryPort changeItemRepo) {
        this.changeRequestRepo = changeRequestRepo;
        this.changeItemRepo = changeItemRepo;
    }

    @Transactional
    public EventChangeRequestResponse execute(CreateEventRequest request) {
        var eventId = EventId.of(UUID.randomUUID());
        var requestId = EventChangeRequestId.of(UUID.randomUUID());
        var itemId = EventChangeItemId.of(UUID.randomUUID());
        var userId = UserId.of(request.userId());

        var changeRequest = EventChangeRequest.create(requestId, eventId, userId, request.justification());
        var changeItem = EventChangeItem.createForNewEvent(
                itemId, requestId,
                request.title(), request.description(),
                request.startAt(), request.endAt(),
                request.isAllDay(), request.recurrenceRule()
        );

        changeRequestRepo.save(changeRequest);
        changeItemRepo.save(changeItem);

        return EventChangeRequestMapper.toResponse(changeRequest);
    }
}
