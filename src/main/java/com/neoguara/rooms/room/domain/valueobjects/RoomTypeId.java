package com.neoguara.rooms.room.domain.valueobjects;
import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public record RoomTypeId(UUID id) {
    public RoomTypeId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("RoomTypeId must not be null"));
    }

    public RoomTypeId() {
        this(UUID.randomUUID());
    }

    public static RoomTypeId of(UUID id) {
        return new RoomTypeId(id);
    }
}
