package com.neoguara.rooms.room.application.usecases.roomtype;

import com.neoguara.rooms.room.application.ports.RoomTypeRepositoryPort;
import com.neoguara.rooms.room.domain.entities.RoomType;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteRoomTypeUseCase {

    private final RoomTypeRepositoryPort repository;

    public DeleteRoomTypeUseCase(RoomTypeRepositoryPort repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        RoomType roomType = repository.findById(RoomTypeId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", id));
        roomType.deactivate();
        repository.save(roomType);
    }
}
