package com.neoguara.rooms.room.application.mappers;

import com.neoguara.rooms.room.application.dtos.room.CreateRoomRequest;
import com.neoguara.rooms.room.application.dtos.room.RoomResponse;
import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;

public class RoomMapper {

    private RoomMapper() {}

    public static Room toDomain(CreateRoomRequest request) {
        return Room.create(
                request.name(),
                request.code(),
                request.type(),
                RoomTypeId.of(request.roomTypeId()),
                BuildingId.of(request.buildingId()),
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
                room.getRoomTypeId().id(),
                room.getBuildingId().id(),
                room.getFloor(),
                room.getCapacity(),
                room.getStatus(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }
}
