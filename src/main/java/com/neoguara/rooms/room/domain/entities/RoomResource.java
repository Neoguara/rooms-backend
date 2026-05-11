package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.room.domain.valueobjects.RoomResourceId;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_resources")
public class RoomResource {

    @EmbeddedId
    private RoomResourceId id;

    @AttributeOverride(name = "id", column = @Column(name = "room_id"))
    private RoomId roomId;

    @AttributeOverride(name = "id", column = @Column(name = "resource_id"))
    private ResourceId resourceId;

    private LocalDateTime createdAt;

    RoomResource() {}

    public RoomResource(RoomId roomId, ResourceId resourceId) {
        this.id = new RoomResourceId();
        this.roomId = roomId;
        this.resourceId = resourceId;
        this.createdAt = LocalDateTime.now();
    }

    public static RoomResource create (RoomId roomId, ResourceId resourceId) {
        return new RoomResource(roomId, resourceId);
    }

    public RoomResourceId getId() {return id;}
    public RoomId getRoomId() {return roomId;}
    public ResourceId getResourceId() {return resourceId;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
