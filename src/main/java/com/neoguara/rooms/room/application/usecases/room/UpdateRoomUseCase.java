package com.neoguara.rooms.room.application.usecases.room;

import com.neoguara.rooms.room.application.dtos.room.RoomResponse;
import com.neoguara.rooms.room.application.dtos.room.UpdateRoomRequest;
import com.neoguara.rooms.room.application.mappers.RoomMapper;
import com.neoguara.rooms.room.application.ports.RoomRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));

        room.update(
                request.name(),
                request.code(),
                RoomTypeId.of(request.roomTypeId()),
                BuildingId.of(request.buildingId()),
                request.floor(),
                request.capacity()
        );

        return RoomMapper.toResponse(repository.save(room));
    }
}
