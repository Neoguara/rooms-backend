package com.neoguara.rooms.room.application.usecases.room;

import com.neoguara.rooms.room.application.dtos.room.RoomResponse;
import com.neoguara.rooms.room.application.dtos.room.UpdateRoomRequest;
import com.neoguara.rooms.room.application.mappers.room.RoomMapper;
import com.neoguara.rooms.room.application.ports.RoomRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateRoomUseCase {

    private final RoomRepositoryPort repository;

    public UpdateRoomUseCase(RoomRepositoryPort repository) {
        this.repository = repository;
    }

    public RoomResponse execute(UUID id, UpdateRoomRequest request) {
        Room room = repository.findById(RoomId.of(id))
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));

        room.update(
                request.name(),
                request.code(),
                request.type(),
                request.building(),
                request.resources(),
                request.floor(),
                request.capacity()
        );

        return RoomMapper.toResponse(repository.save(room));
    }
}
