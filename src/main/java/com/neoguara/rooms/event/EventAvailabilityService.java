package com.neoguara.rooms.event;

import com.neoguara.rooms.event.domain.enums.EventStatus;
import com.neoguara.rooms.event.infrastructure.repositories.EventJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class EventAvailabilityService {

    private final EventJpaRepository eventJpaRepository;

    public EventAvailabilityService(EventJpaRepository eventJpaRepository) {
        this.eventJpaRepository = eventJpaRepository;
    }

    public Set<UUID> findOccupiedRoomIds(LocalDateTime startAt, LocalDateTime endAt) {
        return new HashSet<>(eventJpaRepository.findOccupiedRoomIds(EventStatus.ACTIVE, startAt, endAt));
    }
}
