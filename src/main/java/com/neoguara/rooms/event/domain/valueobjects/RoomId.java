package com.neoguara.rooms.event.domain.valueobjects;

import org.springframework.util.Assert;

import java.util.UUID;

public record RoomId(UUID id) {
    public RoomId {
        Assert.notNull(id, "id must not be null");
    }
    public static RoomId of(UUID id) {
        return new RoomId(id);
    }
}