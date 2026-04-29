package com.neoguara.rooms.event.domain.valueobjects;

import org.springframework.util.Assert;

import java.util.UUID;

public record EventChangeRequestId (UUID id) {
    public EventChangeRequestId {
        Assert.notNull(id, "id must not be null");
    }

    public static EventChangeRequestId of (UUID id) {
        return new EventChangeRequestId(id);
    }

}
