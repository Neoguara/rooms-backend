package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.enums.ResourceStatus;
import com.neoguara.rooms.room.domain.validation.ResourceValidation;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "resources")
public class Resource {
    @EmbeddedId
    private ResourceId id;

    private String name;
    private String description;
    private String icon;
    private ResourceStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    Resource () {}

    private Resource(String name, String description, String icon) {
        this.id = new ResourceId();
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.status = ResourceStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Resource create(String name, String description, String icon) {
        Resource resource = new Resource(name, description, icon);
        Notification notification = Notification.create();
        new ResourceValidation().validate(resource, notification);
        notification.raiseIfHasErrors();
        return resource;
    }

    public void update(String name, String description, String icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        Notification notification = Notification.create();
        new ResourceValidation().validate(this, notification);
        notification.raiseIfHasErrors();
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (status == ResourceStatus.DELETED) throw new InvalidStateException("Deleted resource cannot be modified");
        this.status = ResourceStatus.ACTIVE;
    }

    public void deactivate() {
        if (status == ResourceStatus.DELETED) throw new InvalidStateException("Deleted resource cannot be modified");
        this.status = ResourceStatus.INACTIVE;
    }

    public void delete() {
        this.status = ResourceStatus.DELETED;
    }

    public ResourceId getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public ResourceStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
