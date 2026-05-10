package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
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
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
