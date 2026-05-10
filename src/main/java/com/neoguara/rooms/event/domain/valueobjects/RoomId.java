package com.neoguara.rooms.event.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;

import java.util.UUID;

public record RoomId(UUID id) {
    public RoomId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("RoomId must not be null"));
    }
    public static RoomId of(UUID id) {
        return new RoomId(id);
    }
}