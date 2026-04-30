package com.neoguara.rooms.event.domain.valueobjects;

import org.springframework.util.Assert;

import java.util.UUID;

public record UserId(UUID id) {
    public UserId {
        Assert.notNull(id, "id must not be null");
    }
    public static UserId of(UUID id) {
        return new UserId(id);
    }
}
