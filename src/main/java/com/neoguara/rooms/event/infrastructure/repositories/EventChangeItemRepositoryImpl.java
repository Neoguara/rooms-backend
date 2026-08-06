package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

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
    public List<EventChangeItem> saveAll(List<EventChangeItem> eventChangeItems) {
        return jpaRepository.saveAll(eventChangeItems);
    }

    @Override
    public List<EventChangeItem> findByEventRequestId(EventRequestId eventRequestId) {
        return jpaRepository.findByEventRequestIdOrderByPositionAsc(eventRequestId);
    }

    @Override
    public boolean hasReversal(EventChangeItemId eventChangeItemId) {
        return jpaRepository.existsByReversalOf(eventChangeItemId.id());
    }

    @Override
    public List<EventChangeItem> findByEventRequestIdIn(Collection<EventRequestId> eventRequestIds) {
        if (eventRequestIds.isEmpty()) return List.of();
        return jpaRepository.findByEventRequestIdIn(
                eventRequestIds.stream().map(EventRequestId::id).toList()
        );
    }
}
