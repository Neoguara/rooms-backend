package com.neoguara.rooms.room.application.usecases.roomtype;

import com.neoguara.rooms.room.application.dtos.roomtype.RoomTypeResponse;
import com.neoguara.rooms.room.application.dtos.roomtype.UpdateRoomTypeStatusRequest;
import com.neoguara.rooms.room.application.mappers.RoomTypeMapper;
import com.neoguara.rooms.room.application.ports.RoomTypeRepositoryPort;
import com.neoguara.rooms.room.domain.entities.RoomType;
import com.neoguara.rooms.room.domain.enums.RoomTypeStatus;
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

    public RoomTypeResponse execute(UUID id, UpdateRoomTypeStatusRequest.Status status) {
        RoomType roomType = repository.findById(RoomTypeId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", id));

        switch (status) {
            case ACTIVE -> {
                if (roomType.getStatus() == RoomTypeStatus.ARCHIVED) {
                    roomType.restore();
                } else {
                    roomType.activate();
                }
            }
            case INACTIVE -> roomType.deactivate();
            case ARCHIVED -> roomType.archive();
        }

        return RoomTypeMapper.toResponse(repository.save(roomType));
    }
}
