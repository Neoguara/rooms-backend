package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
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
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
