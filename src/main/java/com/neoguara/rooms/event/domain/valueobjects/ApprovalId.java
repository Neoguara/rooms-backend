package com.neoguara.rooms.event.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;

import java.util.UUID;

public record ApprovalId(UUID id) {

    public ApprovalId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("ApprovalId must not be null"));
    }

    public ApprovalId() {
        this(UUID.randomUUID());
    }

    public static ApprovalId of (UUID id) {
        return new ApprovalId(id);
    }
}
