package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.EventChangeRequestResponse;
import com.neoguara.rooms.event.application.mappers.EventChangeRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventChangeRequestRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetEventChangeRequestUseCase {

    private final EventChangeRequestRepositoryPort repository;
    private final EventChangeItemRepositoryPort changeItemRepository;

    GetEventChangeRequestUseCase(
            EventChangeRequestRepositoryPort repository,
            EventChangeItemRepositoryPort changeItemRepository
    ) {
        this.repository = repository;
        this.changeItemRepository = changeItemRepository;
    }

    public List<EventChangeRequestResponse> findAll() {
        return repository.findAll().stream()
                .map(changeRequest -> {
                    var changeItem = changeItemRepository
                            .findByEventChangeRequestId(changeRequest.getId())
                            .orElse(null);
                    return EventChangeRequestMapper.toResponse(changeRequest, changeItem);
                })
                .toList();
    }
}
