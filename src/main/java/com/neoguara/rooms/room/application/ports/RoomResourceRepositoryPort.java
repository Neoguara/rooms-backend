package com.neoguara.rooms.room.application.ports;

import com.neoguara.rooms.room.domain.entities.RoomResource;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;

import java.util.List;

public interface RoomResourceRepositoryPort {
    RoomResource save(RoomResource roomResource);
    List<RoomResource> findByRoomId(RoomId roomId);
    void deleteByRoomId(RoomId roomId);
}
