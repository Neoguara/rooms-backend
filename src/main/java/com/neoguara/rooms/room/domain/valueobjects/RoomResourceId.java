package com.neoguara.rooms.room.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record RoomResourceId(UUID id) {
    public RoomResourceId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("RoomResourceId must not be null"));
    }

    public RoomResourceId() {
        this(UUID.randomUUID());
    }

    public static RoomResourceId of(UUID id) {
        return new RoomResourceId(id);
    }
}
