package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RejectEventRequestUseCase {

    private final EventRequestRepositoryPort eventRequestRepository;

    public RejectEventRequestUseCase(EventRequestRepositoryPort eventRequestRepository) {
        this.eventRequestRepository = eventRequestRepository;
    }

    @Transactional
    public void execute(UUID eventRequestId) {
        var requestId = EventRequestId.of(eventRequestId);

        var eventRequest = eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Event request", eventRequestId));

        eventRequest.reject();

        eventRequestRepository.save(eventRequest);
    }
}
