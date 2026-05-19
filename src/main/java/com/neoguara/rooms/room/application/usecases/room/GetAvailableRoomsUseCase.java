package com.neoguara.rooms.room.application.usecases.room;

import com.neoguara.rooms.room.application.ports.RoomAvailabilityPort;
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
import com.neoguara.rooms.room.domain.entities.Building;
import com.neoguara.rooms.room.domain.entities.Resource;
import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.entities.RoomType;
import com.neoguara.rooms.room.domain.enums.RoomStatus;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
    private final RoomAvailabilityPort roomAvailabilityPort;

    public GetAvailableRoomsUseCase(
            RoomRepositoryPort roomRepository,
            RoomResourceRepositoryPort roomResourceRepository,
            ResourceRepositoryPort resourceRepository,
            BuildingRepositoryPort buildingRepository,
            RoomTypeRepositoryPort roomTypeRepository,
            RoomAvailabilityPort roomAvailabilityPort) {
        this.roomRepository = roomRepository;
        this.roomResourceRepository = roomResourceRepository;
        this.resourceRepository = resourceRepository;
        this.buildingRepository = buildingRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomAvailabilityPort = roomAvailabilityPort;
    }

    public List<RoomDetailResponse> execute(RoomAvailabilityFilter filter, Set<RoomExpandField> expand) {
        validate(filter);

        Set<UUID> occupiedRoomIds = roomAvailabilityPort.findOccupiedRoomIds(filter.startAt(), filter.endAt());

        boolean hasSearch = filter.search() != null && !filter.search().isBlank();
        Map<UUID, RoomType> roomTypeMap = hasSearch
                ? roomTypeRepository.findAll().stream().collect(Collectors.toMap(rt -> rt.getId().id(), rt -> rt))
                : Map.of();
        Map<UUID, Building> buildingMap = hasSearch
                ? buildingRepository.findAll().stream().collect(Collectors.toMap(b -> b.getId().id(), b -> b))
                : Map.of();

        return roomRepository.findAll().stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                .filter(r -> filter.minCapacity() == null || r.getCapacity() >= filter.minCapacity())
                .filter(r -> hasAnyRoomTypes(r, filter.roomTypeIds()))
                .filter(r -> hasAnyBuildings(r, filter.buildingIds()))
                .filter(r -> !occupiedRoomIds.contains(r.getId().id()))
                .filter(r -> hasAllResources(r.getId(), filter.resourceIds()))
                .filter(r -> matchesSearch(r, filter.search(), roomTypeMap, buildingMap))
                .map(r -> toDetailResponse(r, expand))
                .toList();
    }

    private boolean hasAnyBuildings(Room room, List<UUID> buildingIds) {
        if (buildingIds == null || buildingIds.isEmpty()) return true;
        return buildingIds.stream().anyMatch(id -> id.equals(room.getBuildingId().id()));
    }

    private boolean hasAnyRoomTypes(Room room, List<UUID> roomTypeIds) {
        if (roomTypeIds == null || roomTypeIds.isEmpty()) return true;
        return roomTypeIds.stream().anyMatch(id -> id.equals(room.getRoomTypeId().id()));
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

    private boolean matchesSearch(Room room, String search, Map<UUID, RoomType> roomTypeMap, Map<UUID, Building> buildingMap) {
        if (search == null || search.isBlank()) return true;
        String q = search.toLowerCase();

        if (contains(room.getName(), q) || contains(room.getCode(), q)) return true;

        RoomType roomType = roomTypeMap.get(room.getRoomTypeId().id());
        if (roomType != null && (contains(roomType.getName(), q) || contains(roomType.getDescription(), q))) return true;

        Building building = buildingMap.get(room.getBuildingId().id());
        if (building != null && contains(building.getName(), q)) return true;

        List<ResourceId> roomResourceIds = roomResourceRepository.findByRoomId(room.getId()).stream()
                .map(rr -> rr.getResourceId())
                .toList();
        List<Resource> resources = resourceRepository.findAllById(roomResourceIds);
        return resources.stream().anyMatch(res -> contains(res.getName(), q) || contains(res.getDescription(), q));
    }

    private boolean contains(String value, String q) {
        return value != null && value.toLowerCase().contains(q);
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
