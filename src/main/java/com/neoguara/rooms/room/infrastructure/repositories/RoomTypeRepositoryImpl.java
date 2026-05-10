package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.application.ports.RoomTypeRepositoryPort;
import com.neoguara.rooms.room.domain.entities.RoomType;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoomTypeRepositoryImpl implements RoomTypeRepositoryPort {

    private final RoomTypeJpaRepository jpaRepository;

    public RoomTypeRepositoryImpl(RoomTypeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RoomType save(RoomType roomType) {
        return jpaRepository.save(roomType);
    }

    @Override
    public Optional<RoomType> findById(RoomTypeId id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<RoomType> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void delete(RoomType roomType) {
        jpaRepository.delete(roomType);
    }
}
