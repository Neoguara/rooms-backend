package com.neoguara.rooms.room.application.ports;

import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;

import java.util.List;
import java.util.Optional;

public interface RoomRepositoryPort {
    Room save(Room room);
    Optional<Room> findById(RoomId id);
    List<Room> findAll();
}
