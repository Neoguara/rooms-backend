package com.neoguara.rooms.room.application.mappers;

import com.neoguara.rooms.room.application.dtos.building.BuildingResponse;
import com.neoguara.rooms.room.application.dtos.resource.ResourceResponse;
import com.neoguara.rooms.room.application.dtos.room.CreateRoomRequest;
import com.neoguara.rooms.room.application.dtos.room.RoomDetailResponse;
import com.neoguara.rooms.room.application.dtos.room.RoomResponse;
import com.neoguara.rooms.room.application.dtos.roomtype.RoomTypeResponse;
import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;

import java.util.List;

public class RoomMapper {

    private RoomMapper() {}

    public static Room toDomain(CreateRoomRequest request) {
        return Room.create(
                request.name(),
                request.code(),
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
                room.getRoomTypeId().id(),
                room.getBuildingId().id(),
                room.getFloor(),
                room.getCapacity(),
                room.getStatus(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }

    public static RoomDetailResponse toDetailResponse(
            Room room,
            BuildingResponse building,
            RoomTypeResponse roomType,
            List<ResourceResponse> resources
    ) {
        return new RoomDetailResponse(
                room.getId().id(),
                room.getName(),
                room.getCode(),
                room.getRoomTypeId().id(),
                room.getBuildingId().id(),
                room.getFloor(),
                room.getCapacity(),
                room.getStatus(),
                room.getCreatedAt(),
                room.getUpdatedAt(),
                building,
                roomType,
                resources
        );
    }
}
