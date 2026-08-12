package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.EventRequestAuditResponse;
import com.neoguara.rooms.event.application.mappers.EventRequestMapper;
import com.neoguara.rooms.event.application.ports.ApprovalRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetEventRequestAuditUseCase {

    private final EventRequestRepositoryPort repository;
    private final EventChangeItemRepositoryPort changeItemRepository;
    private final ApprovalRepositoryPort approvalRepository;

    GetEventRequestAuditUseCase(
            EventRequestRepositoryPort repository,
            EventChangeItemRepositoryPort changeItemRepository,
            ApprovalRepositoryPort approvalRepository
    ) {
        this.repository = repository;
        this.changeItemRepository = changeItemRepository;
        this.approvalRepository = approvalRepository;
    }

    public EventRequestAuditResponse execute(UUID eventRequestId) {
        var requestId = EventRequestId.of(eventRequestId);

        var eventRequest = repository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Event request", eventRequestId));

        return EventRequestMapper.toAuditResponse(
                eventRequest,
                changeItemRepository.findByEventRequestId(requestId),
                approvalRepository.findByEventRequestId(requestId)
        );
    }
}
