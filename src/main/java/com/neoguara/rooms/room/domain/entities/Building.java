package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.enums.BuildingStatus;
import com.neoguara.rooms.room.domain.validation.BuildingValidator;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "buildings")
public class Building {

    @EmbeddedId
    private BuildingId id;

    private String name;
    private String address;
    private Integer totalFloors;
    private BuildingStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    Building() {}

    private Building(String name, String address, Integer totalFloors) {
        this.id = new BuildingId();
        this.name = name;
        this.address = address;
        this.totalFloors = totalFloors;
        this.status = BuildingStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public static Building create(String name, String address, Integer totalFloors) {
        Building building = new Building(name, address, totalFloors);
        Notification notification = Notification.create();
        new BuildingValidator().validate(building, notification);
        notification.raiseIfHasErrors();
        return building;
    }

    public void update(String name, String address, Integer totalFloors) {
        this.name = name;
        this.address = address;
        this.totalFloors = totalFloors;
        Notification notification = Notification.create();
        new BuildingValidator().validate(this, notification);
        notification.raiseIfHasErrors();
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.status == BuildingStatus.ARCHIVED) {
            throw new InvalidStateException("Archived building cannot be activated");
        }
        this.status = BuildingStatus.ACTIVE;
    }

    public void deactivate() {
        if (status == BuildingStatus.ARCHIVED) {
            throw new InvalidStateException("Archived building cannot be deactivated");
        }
        this.status = BuildingStatus.INACTIVE;
    }

    public void archive() {
        this.status = BuildingStatus.ARCHIVED;
    }

    public void restore() {
        if (status != BuildingStatus.ARCHIVED) {
            throw new InvalidStateException("Only archived building can be restored");
        }
        this.status = BuildingStatus.ACTIVE;
    }

    public boolean isAvailable() {
        return status == BuildingStatus.ACTIVE;
    }

    public BuildingId getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public Integer getTotalFloors() { return totalFloors; }
    public BuildingStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
