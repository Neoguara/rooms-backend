package com.neoguara.rooms.room.application.usecases.room;

import com.neoguara.rooms.room.application.dtos.room.ReplaceRoomResourcesRequest;
import com.neoguara.rooms.room.application.dtos.room.RoomResourcesResponse;
import com.neoguara.rooms.room.application.ports.ResourceRepositoryPort;
import com.neoguara.rooms.room.application.ports.RoomRepositoryPort;
import com.neoguara.rooms.room.application.ports.RoomResourceRepositoryPort;
import com.neoguara.rooms.room.domain.entities.RoomResource;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReplaceRoomResourcesUseCase {

    private final RoomRepositoryPort roomRepository;
    private final RoomResourceRepositoryPort roomResourceRepository;
    private final ResourceRepositoryPort resourceRepository;

    public ReplaceRoomResourcesUseCase(RoomRepositoryPort roomRepository,
                                       RoomResourceRepositoryPort roomResourceRepository,
                                       ResourceRepositoryPort resourceRepository) {
        this.roomRepository = roomRepository;
        this.roomResourceRepository = roomResourceRepository;
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public RoomResourcesResponse execute(UUID roomId, ReplaceRoomResourcesRequest request) {
        RoomId roomIdEntity = RoomId.of(roomId);

        roomRepository.findById(roomIdEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomId));

        List<ResourceId> newResourceIds = request.resourceIds().stream()
                .map(id -> {
                    ResourceId resourceId = ResourceId.of(id);
                    resourceRepository.findById(resourceId)
                            .orElseThrow(() -> new ResourceNotFoundException("Resource", id));
                    return resourceId;
                })
                .toList();

        roomResourceRepository.deleteByRoomId(roomIdEntity);

        newResourceIds.forEach(resourceId ->
                roomResourceRepository.save(RoomResource.create(roomIdEntity, resourceId))
        );

        return new RoomResourcesResponse(roomId, request.resourceIds());
    }
}
