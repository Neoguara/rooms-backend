package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventChangeRequestRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ApproveEventChangeRequestUseCase {

    private final EventChangeRequestRepositoryPort changeRequestRepo;
    private final EventChangeItemRepositoryPort changeItemRepo;
    private final EventRepositoryPort eventRepo;

    ApproveEventChangeRequestUseCase(EventChangeRequestRepositoryPort changeRequestRepo,
                                     EventChangeItemRepositoryPort changeItemRepo,
                                     EventRepositoryPort eventRepo) {
        this.changeRequestRepo = changeRequestRepo;
        this.changeItemRepo = changeItemRepo;
        this.eventRepo = eventRepo;
    }

    @Transactional
    public void execute(UUID changeRequestId) {
        var requestId = EventChangeRequestId.of(changeRequestId);

        var changeRequest = changeRequestRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Change request not found: " + changeRequestId));

        var changeItem = changeItemRepo.findByEventChangeRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Change item not found for request: " + changeRequestId));

        var event = Event.create(
                changeRequest.getEventId(),
                changeItem.getNewTitle(),
                changeItem.getNewDescription(),
                changeItem.getNewStartAt(),
                changeItem.getNewEndAt(),
                changeItem.getNewIsAllDay(),
                changeItem.getNewRecurrenceRule()
        );

        eventRepo.save(event);
        changeRequest.approve();
        changeRequestRepo.save(changeRequest);
    }
}
