package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.room.domain.valueobjects.RoomResourceId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_resources")
public class RoomResource {

    private RoomResourceId id;

    @AttributeOverride(name = "id", column = @Column(name = "room_id"))
    private RoomId roomId;

    @AttributeOverride(name = "id", column = @Column(name = "resource_id"))
    private ResourceId resourceId;

    private LocalDateTime createdAt;

}
