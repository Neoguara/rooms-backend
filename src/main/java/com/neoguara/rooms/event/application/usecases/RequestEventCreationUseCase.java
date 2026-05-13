package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.CreateEventRequest;
import com.neoguara.rooms.event.application.dtos.CreateEventRequestResponse;
import com.neoguara.rooms.event.application.mappers.CreateEventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventSnapshot;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestEventCreationUseCase {
    private final EventRequestRepositoryPort eventRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;

    public RequestEventCreationUseCase(EventRequestRepositoryPort eventRequestRepository, EventChangeItemRepositoryPort changeItemRepository) {
        this.eventRequestRepository = eventRequestRepository;
        this.changeItemRepository = changeItemRepository;
    }

    @Transactional
    public CreateEventRequestResponse execute (CreateEventRequest request) {

        var changeRequest = CreateEventRequestMapper.toDomain(request);

        var snapshot = EventSnapshot.of(
                RoomId.of(request.roomId()),
                request.title(), request.description(),
                request.startAt(), request.endAt(),
                request.isAllDay(), request.recurrenceRule()
        );
        var eventChangeItem = EventChangeItem.create(changeRequest.getId(), snapshot);

        eventRequestRepository.save(changeRequest);
        changeItemRepository.save(eventChangeItem);

        return CreateEventRequestMapper.toResponse(changeRequest);
    }
}
