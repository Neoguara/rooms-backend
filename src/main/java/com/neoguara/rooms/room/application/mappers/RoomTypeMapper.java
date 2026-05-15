package com.neoguara.rooms.room.application.mappers;

import com.neoguara.rooms.room.application.dtos.roomtype.CreateRoomTypeRequest;
import com.neoguara.rooms.room.application.dtos.roomtype.RoomTypeResponse;
import com.neoguara.rooms.room.domain.entities.RoomType;

public class RoomTypeMapper {

    private RoomTypeMapper() {}

    public static RoomType toDomain(CreateRoomTypeRequest request) {
        return RoomType.create(
                request.name(),
                request.description(),
                request.defaultCapacity(),
                request.color(),
                request.icon()
        );
    }

    public static RoomTypeResponse toResponse(RoomType roomType) {
        return new RoomTypeResponse(
                roomType.getId().id(),
                roomType.getName(),
                roomType.getDescription(),
                roomType.getDefaultCapacity(),
                roomType.getColor(),
                roomType.getIcon(),
                roomType.getStatus(),
                roomType.getCreatedAt(),
                roomType.getUpdatedAt()
        );
    }
}
