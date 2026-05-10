package com.neoguara.rooms.room.application.usecases.roomtype;

import com.neoguara.rooms.room.application.dtos.roomtype.RoomTypeResponse;
import com.neoguara.rooms.room.application.mappers.RoomTypeMapper;
import com.neoguara.rooms.room.application.ports.RoomTypeRepositoryPort;
import com.neoguara.rooms.room.domain.entities.RoomType;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateRoomTypeStatusUseCase {

    private final RoomTypeRepositoryPort repository;

    public UpdateRoomTypeStatusUseCase(RoomTypeRepositoryPort repository) {
        this.repository = repository;
    }

    public RoomTypeResponse execute(UUID id, Boolean active) {
        RoomType roomType = repository.findById(RoomTypeId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", id));

        if (active) {
            roomType.activate();
        } else {
            roomType.deactivate();
        }

        return RoomTypeMapper.toResponse(repository.save(roomType));
    }
}
