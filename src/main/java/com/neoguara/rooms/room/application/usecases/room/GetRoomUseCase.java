package com.neoguara.rooms.room.application.usecases.room;

import com.neoguara.rooms.room.application.dtos.room.RoomResponse;
import com.neoguara.rooms.room.application.mappers.RoomMapper;
import com.neoguara.rooms.room.application.ports.RoomRepositoryPort;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetRoomUseCase {

    private final RoomRepositoryPort repository;

    public GetRoomUseCase(RoomRepositoryPort repository) {
        this.repository = repository;
    }

    public RoomResponse findById(UUID id) {
        return repository.findById(RoomId.of(id))
                .map(RoomMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
    }

    public List<RoomResponse> findAll() {
        return repository.findAll().stream()
                .map(RoomMapper::toResponse)
                .toList();
    }
}
