package com.neoguara.rooms.event.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record EventId(UUID id) {
    public EventId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("EventId must not be null"));
    }

    public EventId() {
        this(UUID.randomUUID());
    }

    public static EventId of (UUID id) {
        return new EventId(id);
    }

}
