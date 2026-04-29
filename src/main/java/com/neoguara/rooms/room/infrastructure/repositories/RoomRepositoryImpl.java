package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.application.ports.RoomRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoomRepositoryImpl implements RoomRepositoryPort {

    private final RoomJpaRepository jpaRepository;

    public RoomRepositoryImpl(RoomJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Room save(Room room) {
        return jpaRepository.save(room);
    }

    @Override
    public Optional<Room> findById(RoomId id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Room> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void delete(Room room) {
        jpaRepository.delete(room);
    }
}
