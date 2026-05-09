package com.neoguara.rooms.room.domain.valueobjects;

import jakarta.persistence.Embeddable;
import org.springframework.util.Assert;

import java.util.UUID;

@Embeddable
public record BuildingId(UUID id) {
    public BuildingId {
        Assert.notNull(id, "id must not be null");
    }

    public BuildingId() {
        this(UUID.randomUUID());
    }

    public static BuildingId of (UUID id) { return  new BuildingId(id); }
}
