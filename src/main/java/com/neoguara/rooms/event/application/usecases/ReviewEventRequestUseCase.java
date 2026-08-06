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
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReviewEventRequestUseCase {

    private final EventRepositoryPort eventRepository;
    private final EventRequestRepositoryPort eventRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;
    private final ApprovalRepositoryPort approvalRepository;

    public ReviewEventRequestUseCase(
            EventRepositoryPort eventRepository,
            EventRequestRepositoryPort eventRequestRepository,
            EventChangeItemRepositoryPort changeItemRepository,
            ApprovalRepositoryPort approvalRepository
    ) {
        this.eventRepository = eventRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.changeItemRepository = changeItemRepository;
        this.approvalRepository = approvalRepository;
    }

    @Transactional
    public EventRequestResponse execute(UUID eventRequestId, UUID reviewedBy, ReviewEventRequest review) {
        var requestId = EventRequestId.of(eventRequestId);

        var eventRequest = eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Event request", eventRequestId));

        validate(review);

        var decidedBy = UserId.of(reviewedBy);
        var changeItems = changeItemRepository.findByEventRequestId(requestId);
        Map<UUID, EventChangeItem> itemsById = changeItems.stream()
                .collect(Collectors.toMap(item -> item.getId().id(), Function.identity()));

        for (var decision : review.decisions()) {
            var item = itemsById.get(decision.itemId());
            if (item == null)
                throw new ResourceNotFoundException(
                        "Change item in event request " + eventRequestId, decision.itemId());

            switch (decision.decision()) {
                case APPROVED -> {
                    item.approve();
                    apply(item);
                }
                case REJECTED -> item.reject();
            }

            changeItemRepository.save(item);
            approvalRepository.save(
                    Approval.of(item.getId(), decidedBy, decision.decision(), decision.comment()));
        }

        return EventRequestMapper.toResponse(eventRequest, changeItems);
    }

    /** Efetiva sobre o evento a alteração descrita pelo item recém-aprovado. */
    private void apply(EventChangeItem item) {
        var after = item.getAfter();

        switch (item.getType()) {
            case CREATE -> eventRepository.save(Event.create(
                    after.getRoomId(),
                    after.getTitle(),
                    after.getDescription(),
                    after.getStartAt(),
                    after.getEndAt(),
                    after.isAllDay(),
                    after.getRecurrenceRule()
            ));
            case UPDATE -> {
                var event = loadEvent(item.getEventId());
                event.update(
                        after.getRoomId(),
                        after.getTitle(),
                        after.getDescription(),
                        after.getStartAt(),
                        after.getEndAt(),
                        after.isAllDay(),
                        after.getRecurrenceRule()
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
                event.reactivate();
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

        List<?> decisions = review.decisions();
        notification.addErrorIf(decisions == null || decisions.isEmpty(),
                "decisions must contain at least one item");

        if (review.decisions() != null) {
            review.decisions().forEach(decision -> notification
                    .addErrorIf(decision.itemId() == null, "itemId is required")
                    .addErrorIf(decision.decision() == null, "decision is required"));
        }

        notification.raiseIfHasErrors();
    }
}
