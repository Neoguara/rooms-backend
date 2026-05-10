package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {

    @EmbeddedId
    private RoomId id;

    private String name;
    private String code;
    private String type;
    private String building;
    private String resources;
    private int floor;
    private int capacity;
    private boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    protected Room() {}

    private Room(String name, String code, String type, String building, String resources, int floor, int capacity) {
        this.id = new RoomId();
        this.name = name;
        this.code = code;
        this.type = type;
        this.building = building;
        this.resources = resources;
        this.floor = floor;
        this.capacity = capacity;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public static Room create(String name, String code, String type, String building, String resources, int floor, int capacity) {
        Notification notification = Notification.create()
                .addErrorIf(name == null || name.isBlank(), "name is required")
                .addErrorIf(code == null || code.isBlank(), "code is required")
                .addErrorIf(capacity <= 0, "capacity must be greater than 0")
                .addErrorIf(floor < 0, "floor must be 0 or greater");
        notification.raiseIfHasErrors();
        return new Room(name, code, type, building, resources, floor, capacity);
    }

    public void update(String name, String code, String type, String building, String resources, int floor, int capacity) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.building = building;
        this.resources = resources;
        this.floor = floor;
        this.capacity = capacity;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }

    public RoomId getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getType() { return type; }
    public String getBuilding() { return building; }
    public String getResources() { return resources; }
    public int getFloor() { return floor; }
    public int getCapacity() { return capacity; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
}
