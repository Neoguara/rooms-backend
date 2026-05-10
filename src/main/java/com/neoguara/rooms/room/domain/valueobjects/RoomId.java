package com.neoguara.rooms.room.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record RoomId(UUID id) {
    public RoomId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("RoomId must not be null"));
    }

    public RoomId() {
        this(UUID.randomUUID());
    }

    public static RoomId of(UUID id) {
        return new RoomId(id);
    }
}
