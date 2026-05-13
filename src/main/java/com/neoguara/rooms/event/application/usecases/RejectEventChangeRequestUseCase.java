package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RejectEventChangeRequestUseCase {

    private final EventRequestRepositoryPort changeRequestRepository;

    public RejectEventChangeRequestUseCase(EventRequestRepositoryPort changeRequestRepository) {
        this.changeRequestRepository = changeRequestRepository;
    }

    @Transactional
    public void execute(UUID changeRequestId) {
        var requestId = EventRequestId.of(changeRequestId);

        var changeRequest = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Change request", changeRequestId));

        changeRequest.reject();

        changeRequestRepository.save(changeRequest);
    }

}
