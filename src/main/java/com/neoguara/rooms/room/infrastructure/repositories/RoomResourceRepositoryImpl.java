package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.application.ports.RoomResourceRepositoryPort;
import com.neoguara.rooms.room.domain.entities.RoomResource;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomResourceRepositoryImpl implements RoomResourceRepositoryPort {

    private final RoomResourceJpaRepository jpaRepository;

    public RoomResourceRepositoryImpl(RoomResourceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RoomResource save(RoomResource roomResource) {
        return jpaRepository.save(roomResource);
    }

    @Override
    public List<RoomResource> findByRoomId(RoomId roomId) {
        return jpaRepository.findByRoomId(roomId);
    }

    @Override
    public void deleteByRoomId(RoomId roomId) {
        jpaRepository.deleteByRoomId(roomId);
    }
}
