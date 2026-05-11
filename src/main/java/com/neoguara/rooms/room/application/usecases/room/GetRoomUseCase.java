package com.neoguara.rooms.room.application.usecases.room;

import com.neoguara.rooms.room.application.dtos.building.BuildingResponse;
import com.neoguara.rooms.room.application.dtos.resource.ResourceResponse;
import com.neoguara.rooms.room.application.dtos.room.RoomDetailResponse;
import com.neoguara.rooms.room.application.dtos.room.RoomExpandField;
import com.neoguara.rooms.room.application.dtos.roomtype.RoomTypeResponse;
import com.neoguara.rooms.room.application.mappers.BuildingMapper;
import com.neoguara.rooms.room.application.mappers.ResourceMapper;
import com.neoguara.rooms.room.application.mappers.RoomMapper;
import com.neoguara.rooms.room.application.mappers.RoomTypeMapper;
import com.neoguara.rooms.room.application.ports.BuildingRepositoryPort;
import com.neoguara.rooms.room.application.ports.ResourceRepositoryPort;
import com.neoguara.rooms.room.application.ports.RoomRepositoryPort;
import com.neoguara.rooms.room.application.ports.RoomResourceRepositoryPort;
import com.neoguara.rooms.room.application.ports.RoomTypeRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GetRoomUseCase {

    private final RoomRepositoryPort roomRepository;
    private final BuildingRepositoryPort buildingRepository;
    private final RoomTypeRepositoryPort roomTypeRepository;
    private final RoomResourceRepositoryPort roomResourceRepository;
    private final ResourceRepositoryPort resourceRepository;

    public GetRoomUseCase(RoomRepositoryPort roomRepository,
                          BuildingRepositoryPort buildingRepository,
                          RoomTypeRepositoryPort roomTypeRepository,
                          RoomResourceRepositoryPort roomResourceRepository,
                          ResourceRepositoryPort resourceRepository) {
        this.roomRepository = roomRepository;
        this.buildingRepository = buildingRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomResourceRepository = roomResourceRepository;
        this.resourceRepository = resourceRepository;
    }

    public RoomDetailResponse findById(UUID id, Set<RoomExpandField> expand) {
        Room room = roomRepository.findById(RoomId.of(id))
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
        return toDetailResponse(room, expand);
    }

    public List<RoomDetailResponse> findAll(Set<RoomExpandField> expand) {
        return roomRepository.findAll().stream()
                .map(room -> toDetailResponse(room, expand))
                .toList();
    }

    private RoomDetailResponse toDetailResponse(Room room, Set<RoomExpandField> expand) {
        BuildingResponse building = expand.contains(RoomExpandField.BUILDING)
                ? buildingRepository.findById(room.getBuildingId()).map(BuildingMapper::toResponse).orElse(null)
                : null;

        RoomTypeResponse roomType = expand.contains(RoomExpandField.ROOM_TYPE)
                ? roomTypeRepository.findById(room.getRoomTypeId()).map(RoomTypeMapper::toResponse).orElse(null)
                : null;

        List<ResourceResponse> resources = expand.contains(RoomExpandField.RESOURCES)
                ? fetchResources(room)
                : null;

        return RoomMapper.toDetailResponse(room, building, roomType, resources);
    }

    private List<ResourceResponse> fetchResources(Room room) {
        List<ResourceId> resourceIds = roomResourceRepository.findByRoomId(room.getId())
                .stream()
                .map(rr -> rr.getResourceId())
                .toList();

        return resourceRepository.findAllById(resourceIds)
                .stream()
                .map(ResourceMapper::toResponse)
                .toList();
    }
}
