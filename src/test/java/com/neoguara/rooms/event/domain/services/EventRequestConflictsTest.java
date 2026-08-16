package com.neoguara.rooms.event.domain.services;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.valueobjects.EventSnapshot;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventRequestConflictsTest {

    private static final RoomId ROOM = RoomId.of(UUID.randomUUID());
    private static final RoomId OTHER_ROOM = RoomId.of(UUID.randomUUID());

    private static final LocalDateTime NINE = LocalDateTime.of(2026, 9, 1, 9, 0);
    private static final LocalDateTime TEN = LocalDateTime.of(2026, 9, 1, 10, 0);
    private static final LocalDateTime ELEVEN = LocalDateTime.of(2026, 9, 1, 11, 0);
    private static final LocalDateTime NOON = LocalDateTime.of(2026, 9, 1, 12, 0);

    private static final RoomOccupancy FREE = (roomId, startAt, endAt) -> List.of();

    private final EventRequestId group = new EventRequestId();

    private static RoomOccupancy agendaWith(Event... events) {
        return (roomId, startAt, endAt) -> List.of(events);
    }

    private Event event(RoomId roomId, String title, LocalDateTime startAt, LocalDateTime endAt) {
        return Event.create(roomId, title, null, startAt, endAt, false, null, FREE);
    }

    private EventSnapshot snapshot(RoomId roomId, String title, LocalDateTime startAt, LocalDateTime endAt) {
        return EventSnapshot.of(roomId, title, null, startAt, endAt, false, null);
    }

    @Test
    void emptyGroupHasNothingToWarnAbout() {
        assertTrue(EventRequestConflicts.preview(List.of(), FREE).isEmpty());
    }

    @Test
    void creationOverAnOccupiedSlotIsWarned() {
        Event occupant = event(ROOM, "Retrospectiva", TEN, NOON);
        var create = EventChangeItem.create(group, 0, snapshot(ROOM, "Planejamento", ELEVEN, NOON));

        var conflicts = EventRequestConflicts.preview(List.of(create), agendaWith(occupant));

        assertEquals(1, conflicts.size());
        assertEquals(occupant.getId().id(), conflicts.getFirst().occupant().id());
        assertEquals("Planejamento", conflicts.getFirst().attempted().title());
    }

    @Test
    void creationInAnotherRoomIsNotWarned() {
        Event occupant = event(OTHER_ROOM, "Retrospectiva", TEN, NOON);
        var create = EventChangeItem.create(group, 0, snapshot(ROOM, "Planejamento", TEN, NOON));

        assertTrue(EventRequestConflicts.preview(List.of(create), agendaWith(occupant)).isEmpty());
    }

    /**
     * O caso que obriga a simulação a respeitar a ordem: sem carregar o que o item anterior
     * liberou, o reaproveitamento do horário viraria um conflito falso.
     */
    @Test
    void slotFreedEarlierInTheGroupCanBeReused() {
        Event occupant = event(ROOM, "Retrospectiva", TEN, NOON);
        var cancel = EventChangeItem.cancel(group, 0, occupant);
        var create = EventChangeItem.create(group, 1, snapshot(ROOM, "Planejamento", TEN, NOON));

        assertTrue(EventRequestConflicts.preview(List.of(cancel, create), agendaWith(occupant)).isEmpty());
    }

    /** E o inverso: reaproveitar antes de liberar continua sendo conflito. */
    @Test
    void reusingASlotBeforeFreeingItIsWarned() {
        Event occupant = event(ROOM, "Retrospectiva", TEN, NOON);
        var create = EventChangeItem.create(group, 0, snapshot(ROOM, "Planejamento", TEN, NOON));
        var cancel = EventChangeItem.cancel(group, 1, occupant);

        assertEquals(1, EventRequestConflicts.preview(List.of(create, cancel), agendaWith(occupant)).size());
    }

    @Test
    void twoOverlappingCreationsInTheSameGroupAreWarned() {
        var first = EventChangeItem.create(group, 0, snapshot(ROOM, "Planejamento", TEN, NOON));
        var second = EventChangeItem.create(group, 1, snapshot(ROOM, "Alinhamento", ELEVEN, NOON));

        var conflicts = EventRequestConflicts.preview(List.of(first, second), FREE);

        assertEquals(1, conflicts.size());
        assertEquals("Alinhamento", conflicts.getFirst().attempted().title());
        assertEquals("Planejamento", conflicts.getFirst().occupant().title());
    }

    @Test
    void backToBackCreationsInTheSameGroupAreNotWarned() {
        var first = EventChangeItem.create(group, 0, snapshot(ROOM, "Planejamento", NINE, TEN));
        var second = EventChangeItem.create(group, 1, snapshot(ROOM, "Alinhamento", TEN, ELEVEN));

        assertTrue(EventRequestConflicts.preview(List.of(first, second), FREE).isEmpty());
    }

    @Test
    void updateDoesNotConflictWithTheEventItChanges() {
        Event event = event(ROOM, "Retrospectiva", TEN, NOON);
        var update = EventChangeItem.update(group, 0, event, snapshot(ROOM, "Retrospectiva", ELEVEN, NOON));

        assertTrue(EventRequestConflicts.preview(List.of(update), agendaWith(event)).isEmpty());
    }

    @Test
    void updateIntoSomeoneElsesSlotIsWarned() {
        Event event = event(ROOM, "Retrospectiva", ELEVEN, NOON);
        Event occupant = event(ROOM, "Daily", NINE, TEN);
        var update = EventChangeItem.update(group, 0, event, snapshot(ROOM, "Retrospectiva", NINE, TEN));

        var conflicts = EventRequestConflicts.preview(List.of(update), agendaWith(event, occupant));

        assertEquals(1, conflicts.size());
        assertEquals("Daily", conflicts.getFirst().occupant().title());
    }

    @Test
    void reactivationIntoATakenSlotIsWarned() {
        Event cancelled = event(ROOM, "Retrospectiva", TEN, NOON);
        cancelled.cancel();
        Event tookTheSlot = event(ROOM, "Planejamento", TEN, NOON);
        var reactivate = EventChangeItem.reactivate(group, 0, cancelled);

        var conflicts = EventRequestConflicts.preview(List.of(reactivate), agendaWith(tookTheSlot));

        assertEquals(1, conflicts.size());
        assertEquals("Planejamento", conflicts.getFirst().occupant().title());
    }

    @Test
    void cancelledEventsInTheAgendaDoNotHoldTheRoom() {
        Event cancelled = event(ROOM, "Retrospectiva", TEN, NOON);
        cancelled.cancel();
        var create = EventChangeItem.create(group, 0, snapshot(ROOM, "Planejamento", TEN, NOON));

        assertTrue(EventRequestConflicts.preview(List.of(create), agendaWith(cancelled)).isEmpty());
    }
}
