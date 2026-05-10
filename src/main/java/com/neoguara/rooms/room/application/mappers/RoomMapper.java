package com.neoguara.rooms.room.application.mappers;

import com.neoguara.rooms.room.application.dtos.CreateRoomRequest;
import com.neoguara.rooms.room.application.dtos.RoomResponse;
import com.neoguara.rooms.room.domain.entities.Room;

public class RoomMapper {

    private RoomMapper() {}

    public static Room toDomain(CreateRoomRequest request) {
        return Room.create(
                request.name(),
                request.code(),
                request.type(),
                request.building(),
                request.resources(),
                request.floor(),
                request.capacity()
        );
    }

    public static RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId().id(),
                room.getName(),
                room.getCode(),
                room.getType(),
                room.getBuilding(),
                room.getResources(),
                room.getFloor(),
                room.getCapacity(),
                room.isActive(),
                room.getCreatedAt()
        );
    }
}
