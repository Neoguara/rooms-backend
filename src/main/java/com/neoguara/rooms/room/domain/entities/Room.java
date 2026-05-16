package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.enums.RoomStatus;
import com.neoguara.rooms.room.domain.validation.RoomValidator;
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

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "room_type_id"))
    private RoomTypeId roomTypeId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "building_id"))
    private BuildingId buildingId;

    private Integer floor;
    private Integer capacity;

    private RoomStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    Room() {}

    private Room(
            String name,
            String code,
            RoomTypeId roomTypeId,
            BuildingId buildingId,
            int floor,
            int capacity
    ) {
        this.id = new RoomId();
        this.name = name;
        this.code = code;
        this.roomTypeId = roomTypeId;
        this.buildingId = buildingId;
        this.floor = floor;
        this.capacity = capacity;
        this.status = RoomStatus.AVAILABLE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Room create (
            String name,
            String code,
            RoomTypeId roomTypeId,
            BuildingId buildingId,
            int floor,
            int capacity
    ) {
        Room room = new Room(name,code,roomTypeId,buildingId,floor,capacity);
        Notification notification = Notification.create();
        new RoomValidator().validate(room, notification);
        notification.raiseIfHasErrors();
        return room;
    }

    public void update (
            String name,
            String code,
            RoomTypeId roomTypeId,
            BuildingId buildingId,
            int floor,
            int capacity
    ) {
        this.name = name;
        this.code = code;
        this.roomTypeId = roomTypeId;
        this.buildingId = buildingId;
        this.floor = floor;
        this.capacity = capacity;
        Notification notification = Notification.create();
        new RoomValidator().validate(this, notification);
        notification.raiseIfHasErrors();
        this.updatedAt = LocalDateTime.now();
    }

    public void activate () {
        if (status == RoomStatus.DELETED) throw new InvalidStateException("Deleted room cannot be modified");
        if (status == RoomStatus.ARCHIVED) throw new InvalidStateException("Archived room cannot be activated");
        this.status = RoomStatus.AVAILABLE;
    }

    public void deactivate () {
        if (status == RoomStatus.DELETED) throw new InvalidStateException("Deleted room cannot be modified");
        if (status == RoomStatus.ARCHIVED) throw new InvalidStateException("Archived room cannot be deactivated");
        this.status = RoomStatus.INACTIVE;
    }

    public void archive() {
        if (status == RoomStatus.DELETED) throw new InvalidStateException("Deleted room cannot be modified");
        this.status = RoomStatus.ARCHIVED;
    }

    public void restore() {
        if (status == RoomStatus.DELETED) throw new InvalidStateException("Deleted room cannot be modified");
        this.status = RoomStatus.AVAILABLE;
    }

    public void putUnderMaintenance() {
        if (status == RoomStatus.DELETED) throw new InvalidStateException("Deleted room cannot be modified");
        if (status == RoomStatus.ARCHIVED) throw new InvalidStateException("Archived room cannot enter maintenance");
        this.status = RoomStatus.MAINTENANCE;
    }

    public void delete() {
        if (status != RoomStatus.ARCHIVED) throw new InvalidStateException("Room must be archived before deletion");
        this.status = RoomStatus.DELETED;
    }

    public RoomId getId() {return id;}
    public String getName() {return name;}
    public String getCode() {return code;}
    public RoomTypeId getRoomTypeId() {return roomTypeId;}
    public BuildingId getBuildingId() {return buildingId;}
    public Integer getFloor() {return floor;}
    public Integer getCapacity() {return capacity;}
    public RoomStatus getStatus() {return status;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
}
