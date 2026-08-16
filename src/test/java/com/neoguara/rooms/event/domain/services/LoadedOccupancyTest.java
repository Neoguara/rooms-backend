package com.neoguara.rooms.event.domain.services;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoadedOccupancyTest {

    private static final RoomId ROOM = RoomId.of(UUID.randomUUID());
    private static final RoomId OTHER_ROOM = RoomId.of(UUID.randomUUID());

    private static final LocalDateTime NINE = LocalDateTime.of(2026, 9, 1, 9, 0);
    private static final LocalDateTime TEN = LocalDateTime.of(2026, 9, 1, 10, 0);
    private static final LocalDateTime ELEVEN = LocalDateTime.of(2026, 9, 1, 11, 0);
    private static final LocalDateTime NOON = LocalDateTime.of(2026, 9, 1, 12, 0);

    private static final RoomOccupancy FREE = (roomId, startAt, endAt) -> List.of();

    private Event event(RoomId roomId, LocalDateTime startAt, LocalDateTime endAt) {
        return Event.create(roomId, "Reunião", null, startAt, endAt, false, null, FREE);
    }

    @Test
    void keepsOnlyWhatSharesTheRoomAndTheWindow() {
        Event inWindow = event(ROOM, TEN, NOON);
        Event otherRoom = event(OTHER_ROOM, TEN, NOON);
        Event before = event(ROOM, NINE, TEN);

        var occupancy = LoadedOccupancy.of(List.of(inWindow, otherRoom, before));

        var found = occupancy.occupying(ROOM, ELEVEN, NOON);

        assertEquals(1, found.size());
        assertEquals(inWindow.getId(), found.getFirst().getId());
    }

    @Test
    void backToBackEventIsOutsideTheWindow() {
        Event before = event(ROOM, NINE, TEN);
        var occupancy = LoadedOccupancy.of(List.of(before));

        assertTrue(occupancy.occupying(ROOM, TEN, ELEVEN).isEmpty());
    }

    @Test
    void emptyAgendaAnswersNothing() {
        assertTrue(LoadedOccupancy.of(List.of()).occupying(ROOM, NINE, NOON).isEmpty());
    }
}
