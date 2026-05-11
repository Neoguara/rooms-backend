package com.neoguara.rooms.room.application.usecases.room;

import com.neoguara.rooms.room.application.dtos.room.CreateRoomRequest;
import com.neoguara.rooms.room.application.dtos.room.RoomResponse;
import com.neoguara.rooms.room.application.mappers.RoomMapper;
import com.neoguara.rooms.room.application.ports.ResourceRepositoryPort;
import com.neoguara.rooms.room.application.ports.RoomRepositoryPort;
import com.neoguara.rooms.room.application.ports.RoomResourceRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.entities.RoomResource;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateRoomUseCase {

    private final RoomRepositoryPort roomRepository;
    private final RoomResourceRepositoryPort roomResourceRepository;
    private final ResourceRepositoryPort resourceRepository;

    public CreateRoomUseCase(RoomRepositoryPort roomRepository,
                             RoomResourceRepositoryPort roomResourceRepository,
                             ResourceRepositoryPort resourceRepository) {
        this.roomRepository = roomRepository;
        this.roomResourceRepository = roomResourceRepository;
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public RoomResponse execute(CreateRoomRequest request) {
        Room room = RoomMapper.toDomain(request);
        Room saved = roomRepository.save(room);

        if (request.resourceIds() != null) {
            for (var resourceId : request.resourceIds()) {
                ResourceId rid = ResourceId.of(resourceId);
                resourceRepository.findById(rid)
                        .orElseThrow(() -> new ResourceNotFoundException("Resource", resourceId));
                roomResourceRepository.save(RoomResource.create(saved.getId(), rid));
            }
        }

        return RoomMapper.toResponse(saved);
    }
}
