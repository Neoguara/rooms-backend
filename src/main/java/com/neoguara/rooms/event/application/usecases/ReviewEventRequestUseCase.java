package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.dtos.ReviewEventRequest;
import com.neoguara.rooms.event.application.mappers.EventRequestMapper;
import com.neoguara.rooms.event.application.ports.ApprovalRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Approval;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.services.RoomOccupancy;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Decide um grupo inteiro de alterações. Aprovar efetiva todas as alterações do grupo, na ordem em
 * que foram submetidas; rejeitar não efetiva nenhuma. Não há decisão parcial: o grupo é a unidade
 * de aprovação, então basta uma alteração não poder ser aplicada para que nada seja gravado.
 */
@Service
public class ReviewEventRequestUseCase {

    private final EventRepositoryPort eventRepository;
    private final EventRequestRepositoryPort eventRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;
    private final ApprovalRepositoryPort approvalRepository;
    private final RoomOccupancy roomOccupancy;

    public ReviewEventRequestUseCase(
            EventRepositoryPort eventRepository,
            EventRequestRepositoryPort eventRequestRepository,
            EventChangeItemRepositoryPort changeItemRepository,
            ApprovalRepositoryPort approvalRepository,
            RoomOccupancy roomOccupancy
    ) {
        this.eventRepository = eventRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.changeItemRepository = changeItemRepository;
        this.approvalRepository = approvalRepository;
        this.roomOccupancy = roomOccupancy;
    }

    @Transactional
    public EventRequestResponse execute(UUID eventRequestId, UUID reviewedBy, ReviewEventRequest review) {
        var requestId = EventRequestId.of(eventRequestId);

        var eventRequest = eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Event request", eventRequestId));

        validate(review);

        var changeItems = changeItemRepository.findByEventRequestId(requestId);

        switch (review.decision()) {
            case APPROVED -> {
                eventRequest.approve();
                changeItems.forEach(this::apply);
                changeItemRepository.saveAll(changeItems);
            }
            case REJECTED -> eventRequest.reject();
        }

        eventRequestRepository.save(eventRequest);
        approvalRepository.save(
                Approval.of(requestId, UserId.of(reviewedBy), review.decision(), review.comment()));

        return EventRequestMapper.toResponse(eventRequest, changeItems);
    }

    /** Efetiva sobre o evento a alteração descrita pelo item do grupo recém-aprovado. */
    private void apply(EventChangeItem item) {
        var after = item.getAfter();

        switch (item.getType()) {
            case CREATE -> {
                var created = eventRepository.save(Event.create(
                        after.getRoomId(),
                        after.getTitle(),
                        after.getDescription(),
                        after.getStartAt(),
                        after.getEndAt(),
                        after.isAllDay(),
                        after.getRecurrenceRule(),
                        after.getSeriesId(),
                        roomOccupancy
                ));
                item.linkCreatedEvent(created.getId());
            }
            case UPDATE -> {
                var event = loadEvent(item.getEventId());
                event.update(
                        after.getRoomId(),
                        after.getTitle(),
                        after.getDescription(),
                        after.getStartAt(),
                        after.getEndAt(),
                        after.isAllDay(),
                        after.getRecurrenceRule(),
                        roomOccupancy
                );
                eventRepository.save(event);
            }
            case CANCEL -> {
                var event = loadEvent(item.getEventId());
                event.cancel();
                eventRepository.save(event);
            }
            case REACTIVATE -> {
                var event = loadEvent(item.getEventId());
                event.reactivate(roomOccupancy);
                eventRepository.save(event);
            }
            case DISCARD -> {
                var event = loadEvent(item.getEventId());
                event.discard();
                eventRepository.save(event);
            }
        }
    }

    private Event loadEvent(EventId eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId.id()));
    }

    private void validate(ReviewEventRequest review) {
        Notification notification = Notification.create();
        notification.addErrorIf(review.decision() == null, "decision is required");
        notification.raiseIfHasErrors();
    }
}
