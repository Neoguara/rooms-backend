package com.neoguara.rooms.room.application.usecases.roomtype;

import com.neoguara.rooms.room.application.dtos.roomtype.CreateRoomTypeRequest;
import com.neoguara.rooms.room.application.dtos.roomtype.RoomTypeResponse;
import com.neoguara.rooms.room.application.mappers.roomtype.RoomTypeMapper;
import com.neoguara.rooms.room.application.ports.RoomTypeRepositoryPort;
import com.neoguara.rooms.room.domain.entities.RoomType;
import org.springframework.stereotype.Service;

@Service
public class CreateRoomTypeUseCase {

    private final RoomTypeRepositoryPort repository;

    public CreateRoomTypeUseCase(RoomTypeRepositoryPort repository) {
        this.repository = repository;
    }

    public RoomTypeResponse execute(CreateRoomTypeRequest request) {
        RoomType roomType = RoomTypeMapper.toDomain(request);
        RoomType saved = repository.save(roomType);
        return RoomTypeMapper.toResponse(saved);
    }
}
