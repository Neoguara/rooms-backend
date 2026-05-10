package com.neoguara.rooms.room.domain.valueobjects;

import jakarta.persistence.Embeddable;
import org.springframework.util.Assert;

import java.util.UUID;

@Embeddable
public record RoomResourceId(UUID id) {
    public RoomResourceId {
        Assert.notNull(id, "id must not be null");
    }

    public RoomResourceId() {
        this(UUID.randomUUID());
    }

    public static RoomResourceId of(UUID id) {
        return new RoomResourceId(id);
    }
}
