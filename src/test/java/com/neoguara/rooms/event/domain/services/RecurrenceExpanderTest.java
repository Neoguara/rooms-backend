package com.neoguara.rooms.event.domain.services;

import com.neoguara.rooms.event.domain.services.RecurrenceExpander.Occurrence;
import com.neoguara.rooms.event.domain.valueobjects.RecurrenceRule;
import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecurrenceExpanderTest {

    private List<LocalDateTime> startsOf(String rule, LocalDateTime startAt, LocalDateTime endAt) {
        return RecurrenceExpander.expand(RecurrenceRule.parse(rule), startAt, endAt).stream()
                .map(Occurrence::startAt)
                .toList();
    }

    @Test
    void firstOccurrenceIsTheEventItself() {
        var start = LocalDateTime.of(2026, 9, 1, 9, 0);
        var occurrences = RecurrenceExpander.expand(
                RecurrenceRule.parse("FREQ=DAILY;COUNT=3"), start, start.plusHours(1));

        assertEquals(3, occurrences.size());
        assertEquals(start, occurrences.getFirst().startAt());
    }

    @Test
    void everyOccurrenceKeepsTheDurationOfTheFirst() {
        var start = LocalDateTime.of(2026, 9, 1, 9, 0);
        var occurrences = RecurrenceExpander.expand(
                RecurrenceRule.parse("FREQ=DAILY;COUNT=3"), start, start.plusMinutes(90));

        occurrences.forEach(o -> assertEquals(90, java.time.Duration.between(o.startAt(), o.endAt()).toMinutes()));
    }

    @Test
    void dailyWalksOneDayAtATime() {
        var start = LocalDateTime.of(2026, 9, 1, 9, 0);
        assertEquals(
                List.of(start, start.plusDays(1), start.plusDays(2)),
                startsOf("FREQ=DAILY;COUNT=3", start, start.plusHours(1)));
    }

    @Test
    void intervalSkipsPeriods() {
        var start = LocalDateTime.of(2026, 9, 1, 9, 0);
        assertEquals(
                List.of(start, start.plusDays(3), start.plusDays(6)),
                startsOf("FREQ=DAILY;INTERVAL=3;COUNT=3", start, start.plusHours(1)));
    }

    @Test
    void weeklyWithoutByDayRepeatsOnTheSameWeekday() {
        var start = LocalDateTime.of(2026, 9, 1, 9, 0); // terça
        assertEquals(
                List.of(start, start.plusWeeks(1), start.plusWeeks(2)),
                startsOf("FREQ=WEEKLY;COUNT=3", start, start.plusHours(1)));
    }

    /** 01/09/2026 é terça: a série pega quarta e sexta da mesma semana, depois a segunda seguinte. */
    @Test
    void weeklyByDayEmitsEachListedDayInWeekOrder() {
        var start = LocalDateTime.of(2026, 9, 1, 9, 0);
        assertEquals(
                List.of(
                        LocalDateTime.of(2026, 9, 2, 9, 0),
                        LocalDateTime.of(2026, 9, 4, 9, 0),
                        LocalDateTime.of(2026, 9, 7, 9, 0),
                        LocalDateTime.of(2026, 9, 9, 9, 0)),
                startsOf("FREQ=WEEKLY;BYDAY=MO,WE,FR;COUNT=4", start, start.plusHours(1)));
    }

    /** O BYDAY pode citar um dia já passado na primeira semana — ele não pode voltar no tempo. */
    @Test
    void weeklyByDaySkipsDaysBeforeTheStart() {
        var start = LocalDateTime.of(2026, 9, 3, 9, 0); // quinta
        assertEquals(
                LocalDateTime.of(2026, 9, 7, 9, 0),
                startsOf("FREQ=WEEKLY;BYDAY=MO;COUNT=1", start, start.plusHours(1)).getFirst());
    }

    @Test
    void weeklyByDayRespectsInterval() {
        var start = LocalDateTime.of(2026, 9, 7, 9, 0); // segunda
        assertEquals(
                List.of(
                        LocalDateTime.of(2026, 9, 7, 9, 0),
                        LocalDateTime.of(2026, 9, 21, 9, 0),
                        LocalDateTime.of(2026, 10, 5, 9, 0)),
                startsOf("FREQ=WEEKLY;INTERVAL=2;BYDAY=MO;COUNT=3", start, start.plusHours(1)));
    }

    @Test
    void untilCutsTheSeriesByDateAndIsInclusive() {
        var start = LocalDateTime.of(2026, 9, 1, 9, 0);
        assertEquals(
                List.of(start, start.plusDays(1), start.plusDays(2)),
                startsOf("FREQ=DAILY;UNTIL=20260903", start, start.plusHours(1)));
    }

    /** Somar mês a mês faria 31/01 virar 28/02 e depois 28/03; a partir da origem, volta para 31/03. */
    @Test
    void monthlyClampsShortMonthsWithoutDrifting() {
        var start = LocalDateTime.of(2026, 1, 31, 9, 0);
        assertEquals(
                List.of(
                        LocalDateTime.of(2026, 1, 31, 9, 0),
                        LocalDateTime.of(2026, 2, 28, 9, 0),
                        LocalDateTime.of(2026, 3, 31, 9, 0)),
                startsOf("FREQ=MONTHLY;COUNT=3", start, start.plusHours(1)));
    }

    /** O motivo de tudo andar em unidades de calendário: o horário local não pode escorregar. */
    @Test
    void timeOfDaySurvivesTheDaylightSavingBoundary() {
        var start = LocalDateTime.of(2026, 10, 10, 14, 0);
        startsOf("FREQ=WEEKLY;COUNT=8", start, start.plusHours(1))
                .forEach(occurrence -> assertEquals(start.toLocalTime(), occurrence.toLocalTime()));
    }

    @Test
    void seriesLongerThanTheCapIsRejectedInsteadOfTruncated() {
        var start = LocalDateTime.of(2026, 1, 1, 9, 0);
        var rule = RecurrenceRule.parse("FREQ=DAILY;UNTIL=20261231");

        var ex = assertThrows(
                DomainValidationException.class,
                () -> RecurrenceExpander.expand(rule, start, start.plusHours(1)));
        assertEquals(1, ex.getNotification().getErrors().size());
    }

    @Test
    void untilBeforeTheStartYieldsOnlyNothing() {
        var start = LocalDateTime.of(2026, 9, 10, 9, 0);
        assertEquals(List.of(), startsOf("FREQ=DAILY;UNTIL=20260901", start, start.plusHours(1)));
    }
}
