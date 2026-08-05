package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.enums.BuildingStatus;
import com.neoguara.rooms.room.domain.enums.ResourceStatus;
import com.neoguara.rooms.room.domain.enums.RoomStatus;
import com.neoguara.rooms.room.domain.enums.RoomTypeStatus;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteRequiresInactiveTest {

    private Room newRoom() {
        return Room.create("Sala 1", "S1", new RoomTypeId(), new BuildingId(), 1, 10);
    }

    private RoomType newRoomType() {
        return RoomType.create("Reunião", "Sala de reunião", "10", "#FFFFFF", "meeting");
    }

    private Building newBuilding() {
        return Building.create("Sede", "Rua 1", 3);
    }

    private Resource newResource() {
        return Resource.create("Projetor", "Projetor full HD", "projector");
    }

    @Test
    void roomCannotBeDeletedWhileAvailable() {
        Room room = newRoom();
        InvalidStateException ex = assertThrows(InvalidStateException.class, room::delete);
        assertEquals("Room must be inactive before deletion", ex.getMessage());
        assertEquals(RoomStatus.AVAILABLE, room.getStatus());
    }

    @Test
    void roomCannotBeDeletedUnderMaintenance() {
        Room room = newRoom();
        room.putUnderMaintenance();
        assertThrows(InvalidStateException.class, room::delete);
        assertEquals(RoomStatus.MAINTENANCE, room.getStatus());
    }

    @Test
    void roomIsDeletedOnceInactive() {
        Room room = newRoom();
        room.deactivate();
        room.delete();
        assertEquals(RoomStatus.DELETED, room.getStatus());
    }

    @Test
    void roomTypeCannotBeDeletedWhileActive() {
        RoomType roomType = newRoomType();
        assertThrows(InvalidStateException.class, roomType::delete);
        assertEquals(RoomTypeStatus.ACTIVE, roomType.getStatus());
    }

    @Test
    void roomTypeIsDeletedOnceInactive() {
        RoomType roomType = newRoomType();
        roomType.deactivate();
        roomType.delete();
        assertEquals(RoomTypeStatus.DELETED, roomType.getStatus());
    }

    @Test
    void buildingCannotBeDeletedWhileActive() {
        Building building = newBuilding();
        assertThrows(InvalidStateException.class, building::delete);
        assertEquals(BuildingStatus.ACTIVE, building.getStatus());
    }

    @Test
    void buildingIsDeletedOnceInactive() {
        Building building = newBuilding();
        building.deactivate();
        building.delete();
        assertEquals(BuildingStatus.DELETED, building.getStatus());
    }

    @Test
    void resourceCannotBeDeletedWhileActive() {
        Resource resource = newResource();
        assertThrows(InvalidStateException.class, resource::delete);
        assertEquals(ResourceStatus.ACTIVE, resource.getStatus());
    }

    @Test
    void resourceIsDeletedOnceInactive() {
        Resource resource = newResource();
        resource.deactivate();
        resource.delete();
        assertEquals(ResourceStatus.DELETED, resource.getStatus());
    }

    @Test
    void deletedEntitiesCannotBeReactivatedOrDeletedAgain() {
        Room room = newRoom();
        room.deactivate();
        room.delete();
        assertThrows(InvalidStateException.class, room::activate);
        assertThrows(InvalidStateException.class, room::delete);
        assertEquals(RoomStatus.DELETED, room.getStatus());
    }
}
