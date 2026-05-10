package com.neoguara.rooms.event.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;

import java.util.UUID;

public record EventChangeRequestId (UUID id) {
    public EventChangeRequestId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("EventChangeRequestId must not be null"));
    }
    public EventChangeRequestId() {
        this(UUID.randomUUID());
    }

    public static EventChangeRequestId of (UUID id) {
        return new EventChangeRequestId(id);
    }

}
