package com.neoguara.rooms.room.application.usecases.room;

import com.neoguara.rooms.event.EventAvailabilityService;
import com.neoguara.rooms.room.application.dtos.building.BuildingResponse;
import com.neoguara.rooms.room.application.dtos.resource.ResourceResponse;
import com.neoguara.rooms.room.application.dtos.room.RoomAvailabilityFilter;
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
import com.neoguara.rooms.room.domain.enums.RoomStatus;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GetAvailableRoomsUseCase {

    private final RoomRepositoryPort roomRepository;
    private final RoomResourceRepositoryPort roomResourceRepository;
    private final ResourceRepositoryPort resourceRepository;
    private final BuildingRepositoryPort buildingRepository;
    private final RoomTypeRepositoryPort roomTypeRepository;
    private final EventAvailabilityService eventAvailabilityService;

    public GetAvailableRoomsUseCase(
            RoomRepositoryPort roomRepository,
            RoomResourceRepositoryPort roomResourceRepository,
            ResourceRepositoryPort resourceRepository,
            BuildingRepositoryPort buildingRepository,
            RoomTypeRepositoryPort roomTypeRepository,
            EventAvailabilityService eventAvailabilityService) {
        this.roomRepository = roomRepository;
        this.roomResourceRepository = roomResourceRepository;
        this.resourceRepository = resourceRepository;
        this.buildingRepository = buildingRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.eventAvailabilityService = eventAvailabilityService;
    }

    public List<RoomDetailResponse> execute(RoomAvailabilityFilter filter, Set<RoomExpandField> expand) {
        validate(filter);

        Set<UUID> occupiedRoomIds = eventAvailabilityService.findOccupiedRoomIds(filter.startAt(), filter.endAt());

        return roomRepository.findAll().stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                .filter(r -> filter.roomTypeId() == null || r.getRoomTypeId().id().equals(filter.roomTypeId()))
                .filter(r -> filter.minCapacity() == null || r.getCapacity() >= filter.minCapacity())
                .filter(r -> !occupiedRoomIds.contains(r.getId().id()))
                .filter(r -> hasAllResources(r.getId(), filter.resourceIds()))
                .map(r -> toDetailResponse(r, expand))
                .toList();
    }

    private boolean hasAllResources(RoomId roomId, List<UUID> requiredResourceIds) {
        if (requiredResourceIds == null || requiredResourceIds.isEmpty()) return true;
        Set<UUID> roomResourceIds = roomResourceRepository.findByRoomId(roomId).stream()
                .map(rr -> rr.getResourceId().id())
                .collect(Collectors.toSet());
        return roomResourceIds.containsAll(requiredResourceIds);
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
        List<ResourceId> resourceIds = roomResourceRepository.findByRoomId(room.getId()).stream()
                .map(rr -> rr.getResourceId())
                .toList();
        return resourceRepository.findAllById(resourceIds).stream()
                .map(ResourceMapper::toResponse)
                .toList();
    }

    private void validate(RoomAvailabilityFilter filter) {
        Notification notification = Notification.create()
                .addErrorIf(filter.startAt() == null, "startAt is required")
                .addErrorIf(filter.endAt() == null, "endAt is required")
                .addErrorIf(
                        filter.startAt() != null && filter.endAt() != null && !filter.startAt().isBefore(filter.endAt()),
                        "startAt must be before endAt"
                );
        notification.raiseIfHasErrors();
    }
}
