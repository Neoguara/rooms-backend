package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.enums.EventStatus;
import com.neoguara.rooms.event.domain.services.RoomOccupancy;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Um evento já terminado aceita as mesmas alterações de um que está por vir: o que decide é o
 * estado, nunca o relógio. Corrigir a agenda de ontem — um título errado, uma sala trocada — é
 * pedido legítimo, e o registro do que aconteceu não vale mais do que a correção.
 */
class ElapsedEventStillAcceptsChangesTest {

    private static final RoomId ROOM = RoomId.of(UUID.randomUUID());
    private static final RoomOccupancy FREE = (roomId, startAt, endAt) -> List.of();

    private Event pastEvent() {
        LocalDateTime endAt = LocalDateTime.now().minusDays(1);
        return Event.create(ROOM, "Reunião", null, endAt.minusHours(1), endAt, false, null, null, FREE);
    }

    @Test
    void elapsedEventCanBeUpdated() {
        Event event = pastEvent();
        assertDoesNotThrow(() -> event.update(
                ROOM, "Outro título", null, event.getStartAt(), event.getEndAt(), false, null, FREE));
        assertEquals("Outro título", event.getTitle());
    }

    @Test
    void elapsedEventCanBeCancelled() {
        Event event = pastEvent();
        assertDoesNotThrow(event::cancel);
        assertEquals(EventStatus.CANCELLED, event.getStatus());
    }

    @Test
    void elapsedEventCanBeReactivated() {
        Event event = pastEvent();
        event.cancel();
        assertDoesNotThrow(() -> event.reactivate(FREE));
        assertEquals(EventStatus.ACTIVE, event.getStatus());
    }

    @Test
    void elapsedEventCanStillBeDiscarded() {
        Event event = pastEvent();
        assertDoesNotThrow(event::discard);
        assertEquals(EventStatus.DISCARDED, event.getStatus());
    }
}
