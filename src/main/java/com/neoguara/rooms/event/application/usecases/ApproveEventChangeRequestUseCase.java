package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventChangeRequestRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.enums.EventChangeType;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ApproveEventChangeRequestUseCase {

    private final EventChangeRequestRepositoryPort changeRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;
    private final EventRepositoryPort eventRepository;


    public ApproveEventChangeRequestUseCase(EventChangeRequestRepositoryPort changeRequestRepository, EventChangeItemRepositoryPort changeItemRepository, EventRepositoryPort eventRepository) {
        this.changeRequestRepository = changeRequestRepository;
        this.changeItemRepository = changeItemRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public void execute(UUID changeRequestId) {

        var requestId = EventChangeRequestId.of(changeRequestId);

        var changeRequest = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Change request not found: " + changeRequestId));

        var changeItem = changeItemRepository.findByEventChangeRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Change item not found for request: " + changeRequestId));

        changeRequest.approve();
        changeRequestRepository.save(changeRequest);

        var changeType = EventChangeType.valueOf(changeRequest.getType());

        switch (changeType) {
            case CREATE -> eventRepository.save(new Event(
                    changeItem.getNewRoomId(),
                    changeItem.getNewTitle(),
                    changeItem.getNewDescription(),
                    changeItem.getNewStartAt(),
                    changeItem.getNewEndAt(),
                    changeItem.getNewIsAllDay(),
                    changeItem.getNewRecurrenceRule()
            ));
            case UPDATE -> {
                var event = eventRepository.findById(changeRequest.getEventId())
                        .orElseThrow(() -> new IllegalArgumentException("Event not found: " + changeRequest.getEventId()));
                event.update(
                        changeItem.getNewRoomId(),
                        changeItem.getNewTitle(),
                        changeItem.getNewDescription(),
                        changeItem.getNewStartAt(),
                        changeItem.getNewEndAt(),
                        changeItem.getNewIsAllDay(),
                        changeItem.getNewRecurrenceRule()
                );
                eventRepository.save(event);
            }
            case DELETE -> {
                var event = eventRepository.findById(changeRequest.getEventId())
                        .orElseThrow(() -> new IllegalArgumentException("Event not found: " + changeRequest.getEventId()));
                event.delete();
                eventRepository.save(event);
            }
        }
    }

}
