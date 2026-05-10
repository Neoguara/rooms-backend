package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.validation.RoomTypeValidation;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
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
    private Boolean active;

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
        this.active = true;
        this.createdAt = LocalDateTime.now();
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
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public RoomTypeId getId() { return id;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public String getDefaultCapacity() {return defaultCapacity;}
    public String getColor() {return color;}
    public String getIcon() {return icon;}
    public Boolean getActive() {return active;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
}
