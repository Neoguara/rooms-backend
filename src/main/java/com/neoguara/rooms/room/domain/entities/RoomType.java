package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.enums.RoomTypeStatus;
import com.neoguara.rooms.room.domain.validation.RoomTypeValidation;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_types")
public class RoomType {
    @EmbeddedId
    private RoomTypeId id;

    private String name;
    private String description;
    private String defaultCapacity;
    private String color;
    private String icon;
    private RoomTypeStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    RoomType () {}

    private RoomType(
            String name,
            String description,
            String defaultCapacity,
            String color,
            String icon
    ) {
        this.id = new RoomTypeId();
        this.name = name;
        this.description = description;
        this.defaultCapacity = defaultCapacity;
        this.color = color;
        this.icon = icon;
        this.status = RoomTypeStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static RoomType create (
            String name,
            String description,
            String defaultCapacity,
            String color,
            String icon
    ) {
        RoomType roomType = new RoomType(name, description, defaultCapacity, color, icon);
        Notification notification = Notification.create();
        new RoomTypeValidation().validate(roomType, notification);
        notification.raiseIfHasErrors();
        return roomType;
    }

    public void update (
            String name,
            String description,
            String defaultCapacity,
            String color,
            String icon
    ) {
        this.name = name;
        this.description = description;
        this.defaultCapacity = defaultCapacity;
        this.color = color;
        this.icon = icon;
        Notification notification = Notification.create();
        new RoomTypeValidation().validate(this, notification);
        notification.raiseIfHasErrors();
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (status == RoomTypeStatus.DELETED) throw new InvalidStateException("Deleted room type cannot be modified");
        if (status == RoomTypeStatus.ARCHIVED) throw new InvalidStateException("Archived room type cannot be activated");
        this.status = RoomTypeStatus.ACTIVE;
    }

    public void deactivate() {
        if (status == RoomTypeStatus.DELETED) throw new InvalidStateException("Deleted room type cannot be modified");
        if (status == RoomTypeStatus.ARCHIVED) throw new InvalidStateException("Archived room type cannot be deactivated");
        this.status = RoomTypeStatus.INACTIVE;
    }

    public void archive() {
        if (status == RoomTypeStatus.DELETED) throw new InvalidStateException("Deleted room type cannot be modified");
        this.status = RoomTypeStatus.ARCHIVED;
    }

    public void restore() {
        if (status == RoomTypeStatus.DELETED) throw new InvalidStateException("Deleted room type cannot be modified");
        if (status != RoomTypeStatus.ARCHIVED) throw new InvalidStateException("Only archived room type can be restored");
        this.status = RoomTypeStatus.ACTIVE;
    }

    public void delete() {
        if (status != RoomTypeStatus.ARCHIVED) throw new InvalidStateException("Room type must be archived before deletion");
        this.status = RoomTypeStatus.DELETED;
    }

    public RoomTypeId getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDefaultCapacity() { return defaultCapacity; }
    public String getColor() { return color; }
    public String getIcon() { return icon; }
    public RoomTypeStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
