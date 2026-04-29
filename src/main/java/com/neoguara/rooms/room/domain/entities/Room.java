package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    protected Room() {}

    public Room(String name, String code, String type, String building, String resources, int floor, int capacity) {
        this.id = new RoomId();
        this.name = name;
        this.code = code;
        this.type = type;
        this.building = building;
        this.resources = resources;
        this.floor = floor;
        this.capacity = capacity;
        this.isActive = true;
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
