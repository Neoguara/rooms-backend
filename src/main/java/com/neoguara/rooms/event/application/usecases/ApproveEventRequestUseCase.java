package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.enums.EventRequestStatus;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ApproveEventRequestUseCase {

    private final EventRequestRepositoryPort eventRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;
    private final EventRepositoryPort eventRepository;

    public ApproveEventRequestUseCase(EventRequestRepositoryPort eventRequestRepository, EventChangeItemRepositoryPort changeItemRepository, EventRepositoryPort eventRepository) {
        this.eventRequestRepository = eventRequestRepository;
        this.changeItemRepository = changeItemRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public void execute(UUID eventRequestId) {

        var requestId = EventRequestId.of(eventRequestId);

        var eventRequest = eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Event request", eventRequestId));

        if (eventRequest.getStatus() == EventRequestStatus.APPROVED) {
            throw new InvalidStateException("Event request already approved: " + eventRequestId);
        }

        var changeItem = changeItemRepository.findByEventRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Change item for request", eventRequestId));

        eventRequest.approve();
        eventRequestRepository.save(eventRequest);

        var after = changeItem.getAfter();

        switch (eventRequest.getType()) {
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
                var event = eventRepository.findById(eventRequest.getEventId())
                        .orElseThrow(() -> new ResourceNotFoundException("Event", eventRequest.getEventId()));
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
                var event = eventRepository.findById(eventRequest.getEventId())
                        .orElseThrow(() -> new ResourceNotFoundException("Event", eventRequest.getEventId()));
                event.cancel();
                eventRepository.save(event);
            }
        }
    }
}
