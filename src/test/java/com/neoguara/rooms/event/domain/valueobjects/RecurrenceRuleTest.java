package com.neoguara.rooms.event.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecurrenceRuleTest {

    @Test
    void readsFrequencyAndCount() {
        var rule = RecurrenceRule.parse("FREQ=WEEKLY;COUNT=4");

        assertEquals(RecurrenceRule.Frequency.WEEKLY, rule.frequency());
        assertEquals(4, rule.count());
        assertEquals(1, rule.interval());
        assertTrue(rule.byDay().isEmpty());
    }

    @Test
    void readsIntervalAndDays() {
        var rule = RecurrenceRule.parse("FREQ=WEEKLY;INTERVAL=2;BYDAY=MO,WE;COUNT=6");

        assertEquals(2, rule.interval());
        assertEquals(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), rule.byDay());
    }

    @Test
    void readsUntilAsPlainDate() {
        assertEquals(LocalDate.of(2026, 9, 30), RecurrenceRule.parse("FREQ=DAILY;UNTIL=20260930").until());
    }

    /** As bibliotecas de calendário emitem a forma longa; só a data interessa. */
    @Test
    void readsUntilInTheFullCalendarForm() {
        assertEquals(
                LocalDate.of(2026, 9, 30),
                RecurrenceRule.parse("FREQ=DAILY;UNTIL=20260930T235959Z").until());
    }

    @Test
    void formatRoundTripsThroughParse() {
        String text = "FREQ=WEEKLY;INTERVAL=2;BYDAY=MO,WE;COUNT=6";
        assertEquals(text, RecurrenceRule.parse(text).format());
    }

    @Test
    void unboundedRecurrenceIsRejected() {
        var ex = assertThrows(DomainValidationException.class, () -> RecurrenceRule.parse("FREQ=DAILY"));
        assertTrue(ex.getNotification().getErrors().contains("recurrence requires COUNT or UNTIL"));
    }

    @Test
    void countAndUntilTogetherAreRejected() {
        assertThrows(
                DomainValidationException.class,
                () -> RecurrenceRule.parse("FREQ=DAILY;COUNT=3;UNTIL=20260930"));
    }

    @Test
    void countAboveTheCapIsRejected() {
        assertThrows(DomainValidationException.class, () -> RecurrenceRule.parse("FREQ=DAILY;COUNT=201"));
    }

    @Test
    void byDayOutsideWeeklyIsRejected() {
        assertThrows(DomainValidationException.class, () -> RecurrenceRule.parse("FREQ=DAILY;BYDAY=MO;COUNT=3"));
    }

    @Test
    void unsupportedPartsAreRejectedInsteadOfIgnored() {
        var ex = assertThrows(
                DomainValidationException.class,
                () -> RecurrenceRule.parse("FREQ=WEEKLY;COUNT=3;BYSETPOS=1"));
        assertTrue(ex.getNotification().getErrors().contains("unsupported recurrence part \"BYSETPOS\""));
    }

    @Test
    void unsupportedFrequencyIsRejected() {
        assertThrows(DomainValidationException.class, () -> RecurrenceRule.parse("FREQ=YEARLY;COUNT=3"));
    }

    @Test
    void blankRuleIsRejected() {
        assertThrows(DomainValidationException.class, () -> RecurrenceRule.parse("  "));
        assertThrows(DomainValidationException.class, () -> RecurrenceRule.parse(null));
    }

    @Test
    void intervalBelowOneIsRejected() {
        assertThrows(DomainValidationException.class, () -> RecurrenceRule.parse("FREQ=DAILY;INTERVAL=0;COUNT=3"));
    }
}
