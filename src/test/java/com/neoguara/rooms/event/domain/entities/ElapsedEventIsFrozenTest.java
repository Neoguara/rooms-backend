package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.enums.EventStatus;
import com.neoguara.rooms.event.domain.services.RoomOccupancy;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElapsedEventIsFrozenTest {

    private static final RoomId ROOM = RoomId.of(UUID.randomUUID());
    private static final RoomOccupancy FREE = (roomId, startAt, endAt) -> List.of();

    private Event eventEndingAt(LocalDateTime endAt) {
        return Event.create(ROOM, "Reunião", null, endAt.minusHours(1), endAt, false, null, null, FREE);
    }

    private Event pastEvent() {
        return eventEndingAt(LocalDateTime.now().minusDays(1));
    }

    private Event futureEvent() {
        return eventEndingAt(LocalDateTime.now().plusDays(1));
    }

    @Test
    void anEventIsElapsedOnceItsEndHasPassed() {
        assertTrue(pastEvent().hasElapsed());
        assertFalse(futureEvent().hasElapsed());
    }

    @Test
    void elapsedEventCannotBeUpdated() {
        Event event = pastEvent();
        var ex = assertThrows(InvalidStateException.class, () -> event.update(
                ROOM, "Outro título", null, event.getStartAt(), event.getEndAt(), false, null, FREE));
        assertEquals("Events that have already ended cannot be updated", ex.getMessage());
    }

    @Test
    void elapsedEventCannotBeCancelled() {
        Event event = pastEvent();
        assertThrows(InvalidStateException.class, event::cancel);
        assertEquals(EventStatus.ACTIVE, event.getStatus());
    }

    /**
     * Reativar um evento vencido também é recusado, mas o estado (CANCELADO + vencido) só surge
     * pela passagem do tempo: {@link Event#cancel()} já recusa o passado, então não há sequência de
     * chamadas que o construa aqui. O que dá para fixar é que reativar segue funcionando enquanto o
     * evento está por vir — a guarda em si é a mesma de {@code update} e {@code cancel}.
     */
    @Test
    void reactivationStillWorksWhileTheEventIsAhead() {
        Event event = futureEvent();
        event.cancel();
        assertDoesNotThrow(() -> event.reactivate(FREE));
        assertEquals(EventStatus.ACTIVE, event.getStatus());
    }

    /**
     * A exceção deliberada: descartar não reescreve a agenda, apaga um registro que nunca deveria
     * ter existido. Sem isso, uma aprovação indevida sobre data passada seria irreversível.
     */
    @Test
    void elapsedEventCanStillBeDiscarded() {
        Event event = pastEvent();
        assertDoesNotThrow(event::discard);
        assertEquals(EventStatus.DISCARDED, event.getStatus());
    }

    @Test
    void futureEventKeepsAcceptingChanges() {
        Event event = futureEvent();
        assertDoesNotThrow(() -> event.update(
                ROOM, "Outro título", null, event.getStartAt(), event.getEndAt(), false, null, FREE));
        assertDoesNotThrow(event::cancel);
    }
}
