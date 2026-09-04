package com.neoguara.rooms.report.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record ReportId(UUID id) {
    public ReportId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("ReportId must not be null"));
    }

    public ReportId() {
        this(UUID.randomUUID());
    }

    public static ReportId of(UUID id) {
        return new ReportId(id);
    }
}
