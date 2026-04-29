package com.neoguara.rooms.event.domain.valueobjects;

import jakarta.persistence.Embeddable;
import org.springframework.util.Assert;

import java.util.UUID;

@Embeddable
public record EventId(UUID id) {
    public EventId {
        Assert.notNull(id, "id must not be null");
    }

    public static EventId of (UUID id) {
        return new EventId(id);
    }

}
