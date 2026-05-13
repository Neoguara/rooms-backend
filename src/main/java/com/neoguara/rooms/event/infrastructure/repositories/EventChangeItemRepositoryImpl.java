package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EventChangeItemRepositoryImpl implements EventChangeItemRepositoryPort {

    private final EventChangeItemJpaRepository jpaRepository;

    public EventChangeItemRepositoryImpl(EventChangeItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public EventChangeItem save(EventChangeItem eventChangeItem) {
        return jpaRepository.save(eventChangeItem);
    }

    @Override
    public Optional<EventChangeItem> findByEventChangeRequestId(EventRequestId eventRequestId) {
        return jpaRepository.findByEventChangeRequestId(eventRequestId);
    }
}
