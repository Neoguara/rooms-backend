package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.enums.EventRequestType;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ApproveEventChangeRequestUseCase {

    private final EventRequestRepositoryPort changeRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;
    private final EventRepositoryPort eventRepository;


    public ApproveEventChangeRequestUseCase(EventRequestRepositoryPort changeRequestRepository, EventChangeItemRepositoryPort changeItemRepository, EventRepositoryPort eventRepository) {
        this.changeRequestRepository = changeRequestRepository;
        this.changeItemRepository = changeItemRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public void execute(UUID changeRequestId) {

        var requestId = EventRequestId.of(changeRequestId);

        var changeRequest = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Change request", changeRequestId));

        if (changeRequest.isApproved()) {
            throw new InvalidStateException("Change request already approved: " + changeRequestId);
        }

        var changeItem = changeItemRepository.findByEventChangeRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Change item for request", changeRequestId));

        changeRequest.approve();
        changeRequestRepository.save(changeRequest);

        var changeType = EventRequestType.valueOf(changeRequest.getType());

        var after = changeItem.getAfter();

        switch (changeType) {
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
                var event = eventRepository.findById(changeRequest.getEventId())
                        .orElseThrow(() -> new ResourceNotFoundException("Event", changeRequest.getEventId()));
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
            case DELETE -> {
                var event = eventRepository.findById(changeRequest.getEventId())
                        .orElseThrow(() -> new ResourceNotFoundException("Event", changeRequest.getEventId()));
                event.delete();
                eventRepository.save(event);
            }
        }
    }

}
