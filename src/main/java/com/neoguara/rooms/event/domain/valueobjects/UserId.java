package com.neoguara.rooms.event.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;

import java.util.UUID;

public record UserId(UUID id) {
    public UserId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("UserId must not be null"));
    }
    public static UserId of(UUID id) {
        return new UserId(id);
    }
}
