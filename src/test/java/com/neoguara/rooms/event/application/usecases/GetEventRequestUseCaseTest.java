package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.services.RoomOccupancy;
import com.neoguara.rooms.event.domain.valueobjects.EventSnapshot;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetEventRequestUseCaseTest {

    private static final RoomId ROOM = RoomId.of(UUID.randomUUID());

    private static final LocalDateTime NINE = LocalDateTime.of(2026, 9, 1, 9, 0);
    private static final LocalDateTime TEN = LocalDateTime.of(2026, 9, 1, 10, 0);
    private static final LocalDateTime ELEVEN = LocalDateTime.of(2026, 9, 1, 11, 0);
    private static final LocalDateTime NOON = LocalDateTime.of(2026, 9, 1, 12, 0);

    private static final RoomOccupancy FREE = (roomId, startAt, endAt) -> List.of();

    private EventRequestRepositoryPort requests;
    private EventChangeItemRepositoryPort changeItems;
    private EventRepositoryPort events;
    private GetEventRequestUseCase useCase;

    @BeforeEach
    void setUp() {
        requests = mock(EventRequestRepositoryPort.class);
        changeItems = mock(EventChangeItemRepositoryPort.class);
        events = mock(EventRepositoryPort.class);
        useCase = new GetEventRequestUseCase(requests, changeItems, events);
    }

    private EventRequest openRequest() {
        return EventRequest.open(UserId.of(UUID.randomUUID()), "porque sim");
    }

    private EventSnapshot snapshot(LocalDateTime startAt, LocalDateTime endAt) {
        return EventSnapshot.of(ROOM, "Planejamento", null, startAt, endAt, false, null, null);
    }

    private Event occupant(LocalDateTime startAt, LocalDateTime endAt) {
        return Event.create(ROOM, "Retrospectiva", null, startAt, endAt, false, null, null, FREE);
    }

    @Test
    void pendingRequestCarriesItsConflicts() {
        var request = openRequest();
        var item = EventChangeItem.create(request.getId(), 0, snapshot(ELEVEN, NOON));

        when(requests.findAll()).thenReturn(List.of(request));
        when(changeItems.findByEventRequestIdIn(any())).thenReturn(List.of(item));
        when(events.findOccupyingBetween(any(), any())).thenReturn(List.of(occupant(TEN, NOON)));

        var responses = useCase.findAll();

        assertEquals(1, responses.size());
        assertEquals(1, responses.getFirst().conflicts().size());
        assertEquals("Retrospectiva", responses.getFirst().conflicts().getFirst().occupantTitle());
    }

    @Test
    void decidedRequestCarriesNoConflicts() {
        var request = openRequest();
        request.approve();
        var item = EventChangeItem.create(request.getId(), 0, snapshot(ELEVEN, NOON));

        when(requests.findAll()).thenReturn(List.of(request));
        when(changeItems.findByEventRequestIdIn(any())).thenReturn(List.of(item));

        var responses = useCase.findAll();

        assertTrue(responses.getFirst().conflicts().isEmpty());
    }

    /** Um grupo decidido não deve nem provocar a leitura da agenda. */
    @Test
    void agendaIsNotLoadedWhenNothingIsPending() {
        var request = openRequest();
        request.reject();
        var item = EventChangeItem.create(request.getId(), 0, snapshot(ELEVEN, NOON));

        when(requests.findAll()).thenReturn(List.of(request));
        when(changeItems.findByEventRequestIdIn(any())).thenReturn(List.of(item));

        useCase.findAll();

        verify(events, times(0)).findOccupyingBetween(any(), any());
    }

    /** A agenda é carregada uma vez só, cobrindo a faixa de todos os pendentes. */
    @Test
    void agendaIsLoadedOnceForTheWholeList() {
        var first = openRequest();
        var second = openRequest();
        var earlyItem = EventChangeItem.create(first.getId(), 0, snapshot(NINE, TEN));
        var lateItem = EventChangeItem.create(second.getId(), 0, snapshot(ELEVEN, NOON));

        when(requests.findAll()).thenReturn(List.of(first, second));
        when(changeItems.findByEventRequestIdIn(any())).thenReturn(List.of(earlyItem, lateItem));
        when(events.findOccupyingBetween(any(), any())).thenReturn(List.of());

        useCase.findAll();

        verify(events, times(1)).findOccupyingBetween(eq(NINE), eq(NOON));
    }
}
