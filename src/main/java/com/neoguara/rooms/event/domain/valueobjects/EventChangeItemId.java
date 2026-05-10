package com.neoguara.rooms.event.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;

import java.util.UUID;

public record EventChangeItemId(UUID id) {
    public EventChangeItemId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("EventChangeItemId must not be null"));
    }

    public EventChangeItemId() {
        this(UUID.randomUUID());
    }

    public static EventChangeItemId of (UUID id) {
        return new EventChangeItemId(id);
    }

}
