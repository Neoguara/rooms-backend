package com.neoguara.rooms.room.domain.valueobjects;

import jakarta.persistence.Embeddable;
import org.springframework.util.Assert;

import java.util.UUID;

@Embeddable
public record RoomId(UUID id) {
    public RoomId {
        Assert.notNull(id, "id must not be null");
    }

    public RoomId() {
        this(UUID.randomUUID());
    }

    public static RoomId of(UUID id) {
        return new RoomId(id);
    }
}
