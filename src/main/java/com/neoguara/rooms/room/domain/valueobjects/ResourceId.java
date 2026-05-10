package com.neoguara.rooms.room.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record ResourceId(UUID id) {
    public ResourceId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("ResourceId must not be null"));
    }

    public ResourceId() {
        this(UUID.randomUUID());
    }

    public static ResourceId of(UUID id) {
        return new ResourceId(id);
    }

}
