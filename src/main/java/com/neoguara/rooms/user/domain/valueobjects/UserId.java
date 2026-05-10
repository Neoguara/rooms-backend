package com.neoguara.rooms.user.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record UserId(UUID id) {
    public UserId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("UserId must not be null"));
    }

    public UserId() {
        this(UUID.randomUUID());
    }

    public static UserId of(UUID id) {
        return new UserId(id);
    }
}
