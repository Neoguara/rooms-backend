package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.CreateEventRequest;
import com.neoguara.rooms.event.application.dtos.CreateEventRequestResponse;
import com.neoguara.rooms.event.application.mappers.CreateEventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestEventCreationUseCase {
    private final EventRequestRepositoryPort changeRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;

    public RequestEventCreationUseCase(EventRequestRepositoryPort changeRequestRepository, EventChangeItemRepositoryPort changeItemRepository) {
        this.changeRequestRepository = changeRequestRepository;
        this.changeItemRepository = changeItemRepository;
    }

    @Transactional
    public CreateEventRequestResponse execute (CreateEventRequest request) {

        var changeRequest = CreateEventRequestMapper.toDomain(request);

        var eventChangeItem = EventChangeItem.create(
                changeRequest.getId(),
                RoomId.of(request.roomId()),
                request.title(),
                request.description(),
                request.startAt(),
                request.endAt(),
                request.isAllDay(),
                request.recurrenceRule()
        );

        changeRequestRepository.save(changeRequest);
        changeItemRepository.save(eventChangeItem);

        return CreateEventRequestMapper.toResponse(changeRequest);
    }
}
