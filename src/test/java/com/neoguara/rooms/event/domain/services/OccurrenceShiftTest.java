package com.neoguara.rooms.event.domain.services;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OccurrenceShiftTest {

    private static final LocalDateTime TUESDAY_TWO_PM = LocalDateTime.of(2026, 9, 1, 14, 0);

    @Test
    void movingTheReferenceForwardOneDayMovesEveryOccurrence() {
        var shift = OccurrenceShift.between(
                TUESDAY_TWO_PM,
                LocalDateTime.of(2026, 9, 2, 14, 0),
                LocalDateTime.of(2026, 9, 2, 15, 0));

        assertEquals(LocalDateTime.of(2026, 9, 9, 14, 0), shift.startFrom(TUESDAY_TWO_PM.plusWeeks(1)));
        assertEquals(LocalDateTime.of(2026, 9, 16, 14, 0), shift.startFrom(TUESDAY_TWO_PM.plusWeeks(2)));
    }

    @Test
    void timeOfDayAndDurationTravelWithTheShift() {
        var shift = OccurrenceShift.between(
                TUESDAY_TWO_PM,
                LocalDateTime.of(2026, 9, 2, 15, 0),
                LocalDateTime.of(2026, 9, 2, 17, 0));

        assertEquals(LocalDateTime.of(2026, 9, 9, 15, 0), shift.startFrom(TUESDAY_TWO_PM.plusWeeks(1)));
        assertEquals(LocalDateTime.of(2026, 9, 9, 17, 0), shift.endFrom(TUESDAY_TWO_PM.plusWeeks(1)));
    }

    @Test
    void aShiftThatOnlyChangesTheHourKeepsEveryDate() {
        var shift = OccurrenceShift.between(
                TUESDAY_TWO_PM,
                LocalDateTime.of(2026, 9, 1, 16, 0),
                LocalDateTime.of(2026, 9, 1, 17, 0));

        assertEquals(LocalDateTime.of(2026, 9, 8, 16, 0), shift.startFrom(TUESDAY_TWO_PM.plusWeeks(1)));
    }

    @Test
    void shiftingBackwardsWorksTheSameWay() {
        var shift = OccurrenceShift.between(
                TUESDAY_TWO_PM,
                LocalDateTime.of(2026, 8, 31, 14, 0),
                LocalDateTime.of(2026, 8, 31, 15, 0));

        assertEquals(LocalDateTime.of(2026, 9, 7, 14, 0), shift.startFrom(TUESDAY_TWO_PM.plusWeeks(1)));
    }

    /**
     * O motivo de o deslocamento guardar dias de calendário, e não uma duração corrida: somar 24h
     * atravessa a virada do horário de verão e desloca o horário local da série inteira.
     */
    @Test
    void timeOfDaySurvivesTheDaylightSavingBoundary() {
        var october = LocalDateTime.of(2026, 10, 10, 14, 0);
        var shift = OccurrenceShift.between(
                october, october.plusDays(1), october.plusDays(1).plusHours(1));

        for (int week = 0; week < 8; week++) {
            assertEquals(
                    october.toLocalTime(),
                    shift.startFrom(october.plusWeeks(week)).toLocalTime());
        }
    }
}
