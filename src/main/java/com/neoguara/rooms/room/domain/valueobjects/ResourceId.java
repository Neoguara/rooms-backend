package com.neoguara.rooms.room.domain.valueobjects;

import jakarta.persistence.Embeddable;
import org.springframework.util.Assert;

import java.util.UUID;

@Embeddable
public record ResourceId(UUID id) {
    public ResourceId {
        Assert.notNull(id, "id must not be null");
    }

    public ResourceId() {
        this(UUID.randomUUID());
    }

    public static ResourceId of(UUID id) {
        return new ResourceId(id);
    }

}
