package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.CancelEventChange;
import com.neoguara.rooms.event.application.dtos.ChangeScope;
import com.neoguara.rooms.event.application.dtos.CreateEventChange;
import com.neoguara.rooms.event.application.dtos.EventChangeRequest;
import com.neoguara.rooms.event.application.dtos.SubmitEventRequest;
import com.neoguara.rooms.event.application.dtos.UpdateEventChange;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.enums.EventChangeType;
import com.neoguara.rooms.event.domain.services.RoomOccupancy;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import com.neoguara.rooms.event.domain.valueobjects.SeriesId;
import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestEventChangesUseCaseTest {

    private static final UUID ROOM = UUID.randomUUID();
    private static final UUID AUTHOR = UUID.randomUUID();
    private static final LocalDateTime TUESDAY_NINE = LocalDateTime.of(2026, 9, 1, 9, 0);

    private static final RoomOccupancy FREE = (roomId, startAt, endAt) -> List.of();

    private EventRepositoryPort events;
    private RequestEventChangesUseCase useCase;

    @BeforeEach
    void setUp() {
        events = mock(EventRepositoryPort.class);
        var requests = mock(EventRequestRepositoryPort.class);
        var changeItems = mock(EventChangeItemRepositoryPort.class);
        useCase = new RequestEventChangesUseCase(events, requests, changeItems, FREE);
    }

    private CreateEventChange creation(String recurrenceRule) {
        return new CreateEventChange(
                "Daily", null, TUESDAY_NINE, TUESDAY_NINE.plusMinutes(30), false, recurrenceRule, ROOM);
    }

    private Event occurrenceOf(SeriesId seriesId, LocalDateTime startAt) {
        return Event.create(RoomId.of(ROOM), "Daily", null, startAt, startAt.plusMinutes(30),
                false, "FREQ=WEEKLY;COUNT=3", seriesId, FREE);
    }

    /** Com BYDAY, que é o único caso em que a regra é reescrita ao mover a série. */
    private Event weeklyOccurrence(SeriesId seriesId, LocalDateTime startAt) {
        return Event.create(RoomId.of(ROOM), "Daily", null, startAt, startAt.plusMinutes(30),
                false, "FREQ=WEEKLY;BYDAY=TU;COUNT=2", seriesId, FREE);
    }

    private SubmitEventRequest submit(EventChangeRequest... changes) {
        return new SubmitEventRequest("porque sim", List.of(changes));
    }

    @Test
    void creationWithoutRecurrenceStaysASingleChange() {
        var response = useCase.execute(AUTHOR, submit(creation(null)));

        assertEquals(1, response.changes().size());
        assertEquals(TUESDAY_NINE, response.changes().getFirst().newStartAt());
    }

    @Test
    void recurrentCreationBecomesOneChangePerOccurrence() {
        var response = useCase.execute(AUTHOR, submit(creation("FREQ=WEEKLY;COUNT=3")));

        assertEquals(3, response.changes().size());
        assertEquals(TUESDAY_NINE, response.changes().get(0).newStartAt());
        assertEquals(TUESDAY_NINE.plusWeeks(1), response.changes().get(1).newStartAt());
        assertEquals(TUESDAY_NINE.plusWeeks(2), response.changes().get(2).newStartAt());
        response.changes().forEach(change -> assertEquals(EventChangeType.CREATE.name(), change.type()));
    }

    /** Só a expansão amarra as ocorrências; sem um id comum não haveria série. */
    @Test
    void everyOccurrenceOfTheSameCreationSharesOneSeries() {
        var response = useCase.execute(AUTHOR, submit(creation("FREQ=DAILY;COUNT=4")));

        var seriesIds = response.changes().stream().map(change -> change.newSeriesId()).distinct().toList();

        assertEquals(1, seriesIds.size());
        assertNotNull(seriesIds.getFirst());
    }

    /** Duas criações recorrentes no mesmo pedido são séries distintas. */
    @Test
    void separateCreationsDoNotShareASeries() {
        var response = useCase.execute(
                AUTHOR, submit(creation("FREQ=DAILY;COUNT=2"), creation("FREQ=DAILY;COUNT=2")));

        assertEquals(4, response.changes().size());
        assertEquals(2, response.changes().stream().map(change -> change.newSeriesId()).distinct().count());
    }

    /** Um evento avulso no meio de recorrentes não ganha série nem atrapalha a numeração. */
    @Test
    void expansionAndPlainChangesShareOneGroup() {
        var response = useCase.execute(AUTHOR, submit(creation("FREQ=DAILY;COUNT=2"), creation(null)));

        assertEquals(3, response.changes().size());
        assertNotNull(response.changes().get(0).newSeriesId());
        assertNotNull(response.changes().get(1).newSeriesId());
        assertEquals(null, response.changes().get(2).newSeriesId());
    }

    @Test
    void invalidRecurrenceRuleIsRejectedOnSubmission() {
        assertThrows(
                DomainValidationException.class,
                () -> useCase.execute(AUTHOR, submit(creation("FREQ=DAILY"))));
    }

    @Test
    void canonicalRuleIsStoredNotTheTextSubmitted() {
        var response = useCase.execute(AUTHOR, submit(creation("freq=daily;interval=1;count=2")));

        assertEquals("FREQ=DAILY;COUNT=2", response.changes().getFirst().newRecurrenceRule());
    }

    @Test
    void cancellingTheWholeSeriesBecomesOneChangePerOccurrence() {
        var series = new SeriesId();
        var first = occurrenceOf(series, TUESDAY_NINE);
        var second = occurrenceOf(series, TUESDAY_NINE.plusWeeks(1));
        var third = occurrenceOf(series, TUESDAY_NINE.plusWeeks(2));

        when(events.findById(any())).thenReturn(Optional.of(second));
        when(events.findBySeriesId(series)).thenReturn(List.of(first, second, third));

        var response = useCase.execute(
                AUTHOR, submit(new CancelEventChange(second.getId().id(), ChangeScope.ALL_OCCURRENCES)));

        assertEquals(3, response.changes().size());
    }

    @Test
    void thisAndFollowingLeavesEarlierOccurrencesAlone() {
        var series = new SeriesId();
        var first = occurrenceOf(series, TUESDAY_NINE);
        var second = occurrenceOf(series, TUESDAY_NINE.plusWeeks(1));
        var third = occurrenceOf(series, TUESDAY_NINE.plusWeeks(2));

        when(events.findById(any())).thenReturn(Optional.of(second));
        when(events.findBySeriesId(series)).thenReturn(List.of(first, second, third));

        var response = useCase.execute(
                AUTHOR, submit(new CancelEventChange(second.getId().id(), ChangeScope.THIS_AND_FOLLOWING)));

        assertEquals(2, response.changes().size());
        assertEquals(TUESDAY_NINE.plusWeeks(1), response.changes().getFirst().oldStartAt());
    }

    /** Um cancelamento avulso anterior não pode derrubar o cancelamento da série inteira. */
    @Test
    void alreadyCancelledOccurrencesAreSkippedInBulk() {
        var series = new SeriesId();
        var first = occurrenceOf(series, TUESDAY_NINE);
        var second = occurrenceOf(series, TUESDAY_NINE.plusWeeks(1));
        first.cancel();

        when(events.findById(any())).thenReturn(Optional.of(second));
        when(events.findBySeriesId(series)).thenReturn(List.of(first, second));

        var response = useCase.execute(
                AUTHOR, submit(new CancelEventChange(second.getId().id(), ChangeScope.ALL_OCCURRENCES)));

        assertEquals(1, response.changes().size());
    }

    @Test
    void bulkScopeOverAStandaloneEventIsRejected() {
        var standalone = Event.create(RoomId.of(ROOM), "Avulso", null,
                TUESDAY_NINE, TUESDAY_NINE.plusHours(1), false, null, null, FREE);
        when(events.findById(any())).thenReturn(Optional.of(standalone));

        assertThrows(InvalidStateException.class, () -> useCase.execute(
                AUTHOR, submit(new CancelEventChange(standalone.getId().id(), ChangeScope.ALL_OCCURRENCES))));
    }

    @Test
    void bulkScopeWithNoApplicableOccurrenceIsRejected() {
        var series = new SeriesId();
        var only = occurrenceOf(series, TUESDAY_NINE);
        only.cancel();

        when(events.findById(any())).thenReturn(Optional.of(only));
        when(events.findBySeriesId(series)).thenReturn(List.of(only));

        assertThrows(InvalidStateException.class, () -> useCase.execute(
                AUTHOR, submit(new CancelEventChange(only.getId().id(), ChangeScope.ALL_OCCURRENCES))));
    }

    private UpdateEventChange move(Event reference, LocalDateTime startAt, Duration length, ChangeScope scope) {
        return new UpdateEventChange(
                reference.getId().id(), reference.getTitle(), null,
                startAt, startAt.plus(length), false,
                reference.getRecurrenceRule(), ROOM, scope);
    }

    /** O caso que motivou o delta: a série inteira sai de terça e vai para quarta. */
    @Test
    void movingTheWholeSeriesShiftsEveryOccurrence() {
        var series = new SeriesId();
        var first = occurrenceOf(series, TUESDAY_NINE);
        var second = occurrenceOf(series, TUESDAY_NINE.plusWeeks(1));
        var third = occurrenceOf(series, TUESDAY_NINE.plusWeeks(2));

        when(events.findById(any())).thenReturn(Optional.of(second));
        when(events.findBySeriesId(series)).thenReturn(List.of(first, second, third));

        var response = useCase.execute(AUTHOR, submit(move(
                second, TUESDAY_NINE.plusWeeks(1).plusDays(1).withHour(10),
                Duration.ofHours(2), ChangeScope.ALL_OCCURRENCES)));

        assertEquals(3, response.changes().size());
        assertEquals(TUESDAY_NINE.plusDays(1).withHour(10), response.changes().get(0).newStartAt());
        assertEquals(TUESDAY_NINE.plusWeeks(1).plusDays(1).withHour(10), response.changes().get(1).newStartAt());
        assertEquals(TUESDAY_NINE.plusWeeks(2).plusDays(1).withHour(10), response.changes().get(2).newStartAt());
        assertEquals(TUESDAY_NINE.plusDays(1).withHour(12), response.changes().get(0).newEndAt());
    }

    /** Mexer a série toda de dia reescreve o BYDAY, senão a regra passaria a mentir. */
    @Test
    void movingTheWholeSeriesRewritesByDay() {
        var series = new SeriesId();
        var first = weeklyOccurrence(series, TUESDAY_NINE);
        var second = weeklyOccurrence(series, TUESDAY_NINE.plusWeeks(1));

        when(events.findById(any())).thenReturn(Optional.of(first));
        when(events.findBySeriesId(series)).thenReturn(List.of(first, second));

        var response = useCase.execute(AUTHOR, submit(move(
                first, TUESDAY_NINE.plusDays(1), Duration.ofMinutes(30), ChangeScope.ALL_OCCURRENCES)));

        assertEquals("FREQ=WEEKLY;BYDAY=WE;COUNT=2", response.changes().getFirst().newRecurrenceRule());
    }

    /** Movendo só parte da série, nenhuma RRULE única descreveria o resultado: a regra fica. */
    @Test
    void movingPartOfTheSeriesLeavesTheRuleAlone() {
        var series = new SeriesId();
        var first = weeklyOccurrence(series, TUESDAY_NINE);
        var second = weeklyOccurrence(series, TUESDAY_NINE.plusWeeks(1));

        when(events.findById(any())).thenReturn(Optional.of(second));
        when(events.findBySeriesId(series)).thenReturn(List.of(first, second));

        var response = useCase.execute(AUTHOR, submit(move(
                second, TUESDAY_NINE.plusWeeks(1).plusDays(1), Duration.ofMinutes(30),
                ChangeScope.THIS_AND_FOLLOWING)));

        assertEquals(1, response.changes().size());
        assertEquals("FREQ=WEEKLY;BYDAY=TU;COUNT=2", response.changes().getFirst().newRecurrenceRule());
    }

    /** Sem escopo, a data enviada vale literalmente, e só para aquele evento. */
    @Test
    void singleOccurrenceUpdateUsesTheDateLiterally() {
        var series = new SeriesId();
        var second = occurrenceOf(series, TUESDAY_NINE.plusWeeks(1));
        when(events.findById(any())).thenReturn(Optional.of(second));

        var response = useCase.execute(AUTHOR, submit(move(
                second, TUESDAY_NINE.plusWeeks(1).plusDays(3), Duration.ofMinutes(30), null)));

        assertEquals(1, response.changes().size());
        assertEquals(TUESDAY_NINE.plusWeeks(1).plusDays(3), response.changes().getFirst().newStartAt());
    }

    @Test
    void changingTheRecurrenceRuleByUpdateIsRejected() {
        var series = new SeriesId();
        var only = occurrenceOf(series, TUESDAY_NINE);
        when(events.findById(any())).thenReturn(Optional.of(only));

        var change = new UpdateEventChange(
                only.getId().id(), "Daily", null, TUESDAY_NINE, TUESDAY_NINE.plusMinutes(30),
                false, "FREQ=DAILY;COUNT=9", ROOM, null);

        assertThrows(InvalidStateException.class, () -> useCase.execute(AUTHOR, submit(change)));
    }

    /** Sem escopo, o comportamento de antes: um item, e o estado é conferido só na aprovação. */
    @Test
    void omittedScopeTouchesOnlyTheEventInformed() {
        var series = new SeriesId();
        var second = occurrenceOf(series, TUESDAY_NINE.plusWeeks(1));
        when(events.findById(any())).thenReturn(Optional.of(second));

        var response = useCase.execute(AUTHOR, submit(new CancelEventChange(second.getId().id(), null)));

        assertEquals(1, response.changes().size());
    }
}
