package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.exceptions.EventConflictException;
import com.neoguara.rooms.event.domain.services.RoomOccupancy;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventConflictTest {

    private static final RoomId ROOM = RoomId.of(UUID.randomUUID());
    private static final RoomId OTHER_ROOM = RoomId.of(UUID.randomUUID());

    private static final LocalDateTime NINE = LocalDateTime.of(2026, 9, 1, 9, 0);
    private static final LocalDateTime TEN = LocalDateTime.of(2026, 9, 1, 10, 0);
    private static final LocalDateTime ELEVEN = LocalDateTime.of(2026, 9, 1, 11, 0);
    private static final LocalDateTime NOON = LocalDateTime.of(2026, 9, 1, 12, 0);

    /** Agenda vazia: nada segura a sala. */
    private static final RoomOccupancy FREE = (roomId, startAt, endAt) -> List.of();

    private static RoomOccupancy taken(Event... events) {
        return (roomId, startAt, endAt) -> List.of(events);
    }

    private Event event(RoomId roomId, LocalDateTime startAt, LocalDateTime endAt, RoomOccupancy occupancy) {
        return Event.create(roomId, "Reunião", null, startAt, endAt, false, null, null, occupancy);
    }

    @Test
    void overlappingEventIsRejected() {
        Event existing = event(ROOM, TEN, NOON, FREE);

        EventConflictException ex = assertThrows(
                EventConflictException.class,
                () -> event(ROOM, ELEVEN, NOON, taken(existing))
        );

        assertEquals(1, ex.getConflicts().size());
        assertEquals(existing.getId().id(), ex.getConflicts().getFirst().occupant().id());
    }

    @Test
    void backToBackEventsDoNotCompete() {
        Event existing = event(ROOM, NINE, TEN, FREE);
        assertDoesNotThrow(() -> event(ROOM, TEN, ELEVEN, taken(existing)));
    }

    @Test
    void eventInAnotherRoomDoesNotConflict() {
        Event existing = event(OTHER_ROOM, TEN, NOON, FREE);
        assertDoesNotThrow(() -> event(ROOM, TEN, NOON, taken(existing)));
    }

    @Test
    void cancelledEventReleasesTheRoom() {
        Event existing = event(ROOM, TEN, NOON, FREE);
        existing.cancel();
        assertDoesNotThrow(() -> event(ROOM, TEN, NOON, taken(existing)));
    }

    @Test
    void completedEventStillHoldsTheRoom() {
        Event existing = event(ROOM, TEN, NOON, FREE);
        existing.complete();
        assertThrows(EventConflictException.class, () -> event(ROOM, TEN, NOON, taken(existing)));
    }

    @Test
    void eventDoesNotConflictWithItselfOnUpdate() {
        Event existing = event(ROOM, TEN, NOON, FREE);
        assertDoesNotThrow(() ->
                existing.update(ROOM, "Reunião remarcada", null, TEN, NOON, false, null, taken(existing)));
    }

    @Test
    void updateIntoAnOccupiedSlotIsRejected() {
        Event other = event(ROOM, NINE, TEN, FREE);
        Event event = event(ROOM, ELEVEN, NOON, FREE);

        assertThrows(
                EventConflictException.class,
                () -> event.update(ROOM, "Reunião", null, NINE, TEN, false, null, taken(other, event))
        );
    }

    @Test
    void reactivationIntoATakenSlotIsRejected() {
        Event event = event(ROOM, TEN, NOON, FREE);
        event.cancel();
        Event tookTheSlot = event(ROOM, TEN, NOON, FREE);

        assertThrows(EventConflictException.class, () -> event.reactivate(taken(tookTheSlot)));
    }

    @Test
    void reactivationIntoAFreeSlotIsAllowed() {
        Event event = event(ROOM, TEN, NOON, FREE);
        event.cancel();
        assertDoesNotThrow(() -> event.reactivate(FREE));
    }

    @Test
    void allConflictsAreReportedNotJustTheFirst() {
        Event first = event(ROOM, NINE, TEN, FREE);
        Event second = event(ROOM, TEN, ELEVEN, FREE);

        EventConflictException ex = assertThrows(
                EventConflictException.class,
                () -> event(ROOM, NINE, NOON, taken(first, second))
        );

        assertEquals(2, ex.getConflicts().size());
        assertEquals(2, ex.getErrors().size());
    }
}
