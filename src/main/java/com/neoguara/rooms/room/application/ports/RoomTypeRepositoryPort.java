package com.neoguara.rooms.room.application.ports;

import com.neoguara.rooms.room.domain.entities.RoomType;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;

import java.util.List;
import java.util.Optional;

public interface RoomTypeRepositoryPort {
    RoomType save(RoomType roomType);
    Optional<RoomType> findById(RoomTypeId id);
    List<RoomType> findAll();
    void delete(RoomType roomType);
}
