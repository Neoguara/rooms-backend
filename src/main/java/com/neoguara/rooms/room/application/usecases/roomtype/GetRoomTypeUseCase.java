package com.neoguara.rooms.room.application.usecases.roomtype;

import com.neoguara.rooms.room.application.dtos.roomtype.RoomTypeResponse;
import com.neoguara.rooms.room.application.mappers.RoomTypeMapper;
import com.neoguara.rooms.room.application.ports.RoomTypeRepositoryPort;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetRoomTypeUseCase {

    private final RoomTypeRepositoryPort repository;

    public GetRoomTypeUseCase(RoomTypeRepositoryPort repository) {
        this.repository = repository;
    }

    public RoomTypeResponse findById(UUID id) {
        return repository.findById(RoomTypeId.of(id))
                .map(RoomTypeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", id));
    }

    public List<RoomTypeResponse> findAll() {
        return repository.findAll().stream()
                .map(RoomTypeMapper::toResponse)
                .toList();
    }
}
