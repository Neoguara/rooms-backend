package com.neoguara.rooms.event.domain.valueobjects;

import org.springframework.util.Assert;

import java.util.UUID;

public record ApprovalId(UUID id) {

    public ApprovalId {
        Assert.notNull(id, "id must not be null");
    }

    public ApprovalId() {
        this(UUID.randomUUID());
    }

    public static ApprovalId of (UUID id) {
        return new ApprovalId(id);
    }
}
