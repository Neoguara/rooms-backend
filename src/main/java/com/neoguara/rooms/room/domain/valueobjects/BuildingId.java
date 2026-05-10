package com.neoguara.rooms.room.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record BuildingId(UUID id) {
    public BuildingId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("BuildingId must not be null"));
    }

    public BuildingId() {
        this(UUID.randomUUID());
    }

    public static BuildingId of (UUID id) { return  new BuildingId(id); }
}
