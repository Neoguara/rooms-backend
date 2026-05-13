package com.neoguara.rooms.event.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;

import java.util.UUID;

public record EventRequestId(UUID id) {
    public EventRequestId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("EventRequestId must not be null"));
    }
    public EventRequestId() {
        this(UUID.randomUUID());
    }

    public static EventRequestId of (UUID id) {
        return new EventRequestId(id);
    }

}
