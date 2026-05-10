package com.neoguara.rooms.room.domain.valueobjects;
import jakarta.persistence.Embeddable;
import org.springframework.util.Assert;
import java.util.UUID;

@Embeddable
public record RoomTypeId(UUID id) {
    public RoomTypeId {
        Assert.notNull(id, "id must not be null");
    }

    public RoomTypeId() {
        this(UUID.randomUUID());
    }

    public static RoomTypeId of(UUID id) {
        return new RoomTypeId(id);
    }
}
