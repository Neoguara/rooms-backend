package com.neoguara.rooms.user.domain.valueobjects;

import jakarta.persistence.Embeddable;
import org.springframework.util.Assert;

import java.util.UUID;

@Embeddable
public record UserId(UUID id) {
    public UserId {
        Assert.notNull(id, "id must not be null");
    }

    public UserId() {
        this(UUID.randomUUID());
    }

    public static UserId of(UUID id) {
        return new UserId(id);
    }
}
