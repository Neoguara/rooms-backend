package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.enums.BuildingStatus;
import com.neoguara.rooms.room.domain.validation.BuildingValidator;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
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
        this.updatedAt = LocalDateTime.now();
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
        if (status == BuildingStatus.DELETED) throw new InvalidStateException("Deleted building cannot be modified");
        this.status = BuildingStatus.ACTIVE;
    }

    public void deactivate() {
        if (status == BuildingStatus.DELETED) throw new InvalidStateException("Deleted building cannot be modified");
        this.status = BuildingStatus.INACTIVE;
    }

    public void delete() {
        if (status != BuildingStatus.INACTIVE) throw new InvalidStateException("Building must be inactive before deletion");
        this.status = BuildingStatus.DELETED;
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
