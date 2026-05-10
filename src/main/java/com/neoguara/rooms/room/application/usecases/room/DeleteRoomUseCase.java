package com.neoguara.rooms.room.application.usecases.room;

import com.neoguara.rooms.room.application.ports.RoomRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteRoomUseCase {

    private final RoomRepositoryPort repository;

    public DeleteRoomUseCase(RoomRepositoryPort repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        Room room = repository.findById(RoomId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        room.softDelete();
        repository.save(room);
    }
}
