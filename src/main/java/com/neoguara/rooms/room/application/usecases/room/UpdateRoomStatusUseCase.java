package com.neoguara.rooms.room.application.usecases.room;

import com.neoguara.rooms.room.application.dtos.room.RoomResponse;
import com.neoguara.rooms.room.application.dtos.room.UpdateRoomStatusRequest;
import com.neoguara.rooms.room.application.mappers.RoomMapper;
import com.neoguara.rooms.room.application.ports.RoomRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateRoomStatusUseCase {

    private final RoomRepositoryPort repository;

    public UpdateRoomStatusUseCase(RoomRepositoryPort repository) {
        this.repository = repository;
    }

    public RoomResponse execute(UUID id, UpdateRoomStatusRequest.Status status) {
        Room room = repository.findById(RoomId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));

        switch (status) {
            case AVAILABLE -> room.activate();
            case INACTIVE -> room.deactivate();
            case MAINTENANCE -> room.putUnderMaintenance();
        }

        return RoomMapper.toResponse(repository.save(room));
    }
}
