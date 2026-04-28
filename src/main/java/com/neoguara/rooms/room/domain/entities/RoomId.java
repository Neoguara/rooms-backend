package com.neoguara.rooms.room.domain.entities;

import org.springframework.util.Assert;

import java.util.UUID;

public record RoomId(UUID id) {
    public RoomId {
        Assert.notNull(id, "id must not be null");
    }

    public RoomId () {
        this(UUID.randomUUID());
    }
}
