package com.neoguara.rooms.room.application.usecases.roomtype;

import com.neoguara.rooms.room.application.dtos.roomtype.RoomTypeResponse;
import com.neoguara.rooms.room.application.dtos.roomtype.UpdateRoomTypeRequest;
import com.neoguara.rooms.room.application.mappers.roomtype.RoomTypeMapper;
import com.neoguara.rooms.room.application.ports.RoomTypeRepositoryPort;
import com.neoguara.rooms.room.domain.entities.RoomType;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateRoomTypeUseCase {

    private final RoomTypeRepositoryPort repository;

    public UpdateRoomTypeUseCase(RoomTypeRepositoryPort repository) {
        this.repository = repository;
    }

    public RoomTypeResponse execute(UUID id, UpdateRoomTypeRequest request) {
        RoomType roomType = repository.findById(RoomTypeId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", id));

        roomType.update(request.name(), request.description(), request.defaultCapacity(), request.color(), request.icon());

        return RoomTypeMapper.toResponse(repository.save(roomType));
    }
}
