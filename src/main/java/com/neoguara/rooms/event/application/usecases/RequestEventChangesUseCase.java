package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.CancelEventChange;
import com.neoguara.rooms.event.application.dtos.CreateEventChange;
import com.neoguara.rooms.event.application.dtos.EventChangeRequest;
import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.dtos.SubmitEventRequest;
import com.neoguara.rooms.event.application.dtos.UpdateEventChange;
import com.neoguara.rooms.event.application.mappers.EventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.valueobjects.EventSnapshot;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RequestEventChangesUseCase {

    private final EventRepositoryPort eventRepository;
    private final EventRequestRepositoryPort eventRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;

    public RequestEventChangesUseCase(
            EventRepositoryPort eventRepository,
            EventRequestRepositoryPort eventRequestRepository,
            EventChangeItemRepositoryPort changeItemRepository
    ) {
        this.eventRepository = eventRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.changeItemRepository = changeItemRepository;
    }

    @Transactional
    public EventRequestResponse execute(SubmitEventRequest request) {
        Notification notification = Notification.create();
        notification.addErrorIf(
                request.changes() == null || request.changes().isEmpty(),
                "changes must contain at least one item"
        );
        notification.raiseIfHasErrors();

        var eventRequest = EventRequest.open(UserId.of(request.userId()), request.justification());

        var changeItems = request.changes().stream()
                .map(change -> toChangeItem(eventRequest.getId(), change))
                .toList();

        eventRequestRepository.save(eventRequest);
        changeItemRepository.saveAll(changeItems);

        return EventRequestMapper.toResponse(eventRequest, changeItems);
    }

    private EventChangeItem toChangeItem(EventRequestId eventRequestId, EventChangeRequest change) {
        return switch (change) {
            case CreateEventChange c -> EventChangeItem.create(
                    eventRequestId,
                    snapshotOf(c.roomId(), c.title(), c.description(),
                            c.startAt(), c.endAt(), c.isAllDay(), c.recurrenceRule())
            );
            case UpdateEventChange u -> EventChangeItem.update(
                    eventRequestId,
                    loadEvent(u.eventId()),
                    snapshotOf(u.roomId(), u.title(), u.description(),
                            u.startAt(), u.endAt(), u.isAllDay(), u.recurrenceRule())
            );
            case CancelEventChange c -> EventChangeItem.cancel(eventRequestId, loadEvent(c.eventId()));
        };
    }

    private Event loadEvent(UUID eventId) {
        return eventRepository.findById(EventId.of(eventId))
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
    }

    private static EventSnapshot snapshotOf(
            UUID roomId, String title, String description,
            LocalDateTime startAt, LocalDateTime endAt,
            Boolean isAllDay, String recurrenceRule
    ) {
        return EventSnapshot.of(RoomId.of(roomId), title, description, startAt, endAt, isAllDay, recurrenceRule);
    }
}
