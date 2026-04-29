package com.neoguara.rooms.room.domain.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {
    @EmbeddedId
    private RoomId id;

    private String name;
    private String code;
    private String type;
    private String building; // pode ser outra entidade
    private String resources; // pode ser outra entidade
    private int flor;
    private int capacity;

    private boolean isAcitive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @CreationTimestamp
    private LocalDateTime updatedAt;

    @CreationTimestamp
    private LocalDateTime deletedAt;

    public Room(String name, String code, String type, String building, String resources, int flor, int capacity) {
        this.id = new RoomId();
        this.name = name;
        this.code = code;
        this.type = type;
        this.building = building;
        this.resources = resources;
        this.flor = flor;
        this.capacity = capacity;
    }
}
