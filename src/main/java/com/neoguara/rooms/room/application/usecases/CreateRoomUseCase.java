package com.neoguara.rooms.room.application.usecases;

import com.neoguara.rooms.room.application.dtos.CreateRoomRequest;
import com.neoguara.rooms.room.application.dtos.RoomResponse;
import com.neoguara.rooms.room.application.mappers.RoomMapper;
import com.neoguara.rooms.room.application.ports.RoomRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Room;
import org.springframework.stereotype.Service;

@Service
public class CreateRoomUseCase {

    private final RoomRepositoryPort repository;

    public CreateRoomUseCase(RoomRepositoryPort repository) {
        this.repository = repository;
    }

    public RoomResponse execute(CreateRoomRequest request) {
        Room room = RoomMapper.toDomain(request);
        Room saved = repository.save(room);
        return RoomMapper.toResponse(saved);
    }
}
