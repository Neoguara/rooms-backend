package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.validation.ResourceValidation;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
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
    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    Resource () {}

    private Resource(String name, String description, String icon) {
        this.id = new ResourceId();
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.createdAt = LocalDateTime.now();
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
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public ResourceId getId() {return id;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public String getIcon() {return icon;}
    public Boolean getActive() {return active;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
}
