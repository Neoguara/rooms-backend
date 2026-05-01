package com.neoguara.rooms.event.domain.valueobjects;

import org.springframework.util.Assert;

import java.util.UUID;

public record EventChangeItemId(UUID id) {
    public EventChangeItemId {
        Assert.notNull(id, "id must not be null");
    }

    public EventChangeItemId() {
        this(UUID.randomUUID());
    }

    public static EventChangeItemId of (UUID id) {
        return new EventChangeItemId(id);
    }

}
