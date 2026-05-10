package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.enums.BuildingStatus;
import com.neoguara.rooms.room.domain.enums.RoomStatus;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {

    @EmbeddedId
    private RoomId id;

    private String name;
    private String code;
    private String type;

    @AttributeOverride(name = "id", column = @Column(name = "room_type_id"))
    private RoomTypeId roomTypeId;

    @AttributeOverride(name = "id", column = @Column(name = "building_id"))
    private BuildingId buildingId;

    private Integer floor;
    private Integer capacity;

    private RoomStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    Room() {}

    public static Room create () {}

    public void update () {}

    public void activate () {
        if (status == RoomStatus.ARCHIVED) {
            throw new InvalidStateException("Archived room cannot be activated");
        }
        this.status = RoomStatus.AVAILABLE;
    }

    public void deactivate () {
        if (status == RoomStatus.ARCHIVED) {
            throw new InvalidStateException("Archived room cannot be deactivated");
        }
        this.status = RoomStatus.INACTIVE;
    }

    public void archive() {
        this.status = RoomStatus.ARCHIVED;
    }

    public void restore() {
        this.status = RoomStatus.AVAILABLE;
    }

    public void putUnderMaintenance() {
        if (status == RoomStatus.ARCHIVED) {
            throw new InvalidStateException("Archived room cannot enter maintenance");
        }

        this.status = RoomStatus.MAINTENANCE;
    }


}
