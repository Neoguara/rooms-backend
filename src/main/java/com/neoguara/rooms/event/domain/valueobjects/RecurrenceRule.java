package com.neoguara.rooms.event.domain.valueobjects;

import com.neoguara.rooms.shared.domain.validation.Notification;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Subconjunto do RRULE do RFC 5545 — o suficiente para reserva de sala, e não mais que isso. A
 * sintaxe é a padrão de propósito, para que o cliente monte e leia a regra com bibliotecas prontas
 * (rrule.js e afins) sem precisar de tradução.
 *
 * <p>Suporta {@code FREQ} (DAILY, WEEKLY, MONTHLY), {@code INTERVAL}, {@code BYDAY} (só em WEEKLY)
 * e um limite, {@code COUNT} ou {@code UNTIL}.
 *
 * <p>O limite é <strong>obrigatório</strong>, ao contrário do RFC: aqui cada ocorrência vira uma
 * linha, e uma recorrência infinita não teria como ser materializada. Pelo mesmo motivo existe o
 * teto de {@value #MAX_OCCURRENCES}.
 */
public record RecurrenceRule(
        Frequency frequency,
        int interval,
        Set<DayOfWeek> byDay,
        Integer count,
        LocalDate until
) {
    public enum Frequency { DAILY, WEEKLY, MONTHLY }

    public static final int MAX_OCCURRENCES = 200;

    private static final Set<String> SUPPORTED_PARTS = Set.of("FREQ", "INTERVAL", "BYDAY", "COUNT", "UNTIL");

    private static final Map<String, DayOfWeek> DAY_BY_CODE = Map.of(
            "MO", DayOfWeek.MONDAY, "TU", DayOfWeek.TUESDAY, "WE", DayOfWeek.WEDNESDAY,
            "TH", DayOfWeek.THURSDAY, "FR", DayOfWeek.FRIDAY, "SA", DayOfWeek.SATURDAY,
            "SU", DayOfWeek.SUNDAY);

    private static final Map<DayOfWeek, String> CODE_BY_DAY = Map.of(
            DayOfWeek.MONDAY, "MO", DayOfWeek.TUESDAY, "TU", DayOfWeek.WEDNESDAY, "WE",
            DayOfWeek.THURSDAY, "TH", DayOfWeek.FRIDAY, "FR", DayOfWeek.SATURDAY, "SA",
            DayOfWeek.SUNDAY, "SU");

    public RecurrenceRule {
        Notification.create()
                .addErrorIf(frequency == null, "recurrence FREQ is required")
                .addErrorIf(interval < 1, "recurrence INTERVAL must be at least 1")
                .addErrorIf(count != null && until != null, "recurrence accepts COUNT or UNTIL, not both")
                .addErrorIf(count == null && until == null, "recurrence requires COUNT or UNTIL")
                .addErrorIf(count != null && count < 1, "recurrence COUNT must be at least 1")
                .addErrorIf(count != null && count > MAX_OCCURRENCES,
                        "recurrence COUNT must not exceed " + MAX_OCCURRENCES)
                .addErrorIf(byDay != null && !byDay.isEmpty() && frequency != Frequency.WEEKLY,
                        "recurrence BYDAY is only supported with FREQ=WEEKLY")
                .raiseIfHasErrors();

        byDay = byDay == null || byDay.isEmpty()
                ? Set.of()
                : Collections.unmodifiableSet(EnumSet.copyOf(byDay));
    }

    /** Lê uma RRULE como {@code FREQ=WEEKLY;INTERVAL=2;BYDAY=MO,WE;COUNT=10}. */
    public static RecurrenceRule parse(String text) {
        Notification notification = Notification.create();
        notification.addErrorIf(text == null || text.isBlank(), "recurrenceRule must not be blank");
        notification.raiseIfHasErrors();

        Map<String, String> parts = split(text, notification);
        parts.keySet().stream()
                .filter(key -> !SUPPORTED_PARTS.contains(key))
                .forEach(key -> notification.addError("unsupported recurrence part \"" + key + "\""));
        notification.raiseIfHasErrors();

        Frequency frequency = parseFrequency(parts.get("FREQ"), notification);
        int interval = parseInterval(parts.get("INTERVAL"), notification);
        Set<DayOfWeek> byDay = parseByDay(parts.get("BYDAY"), notification);
        Integer count = parseNumber(parts.get("COUNT"), "COUNT", notification);
        LocalDate until = parseUntil(parts.get("UNTIL"), notification);
        notification.raiseIfHasErrors();

        return new RecurrenceRule(frequency, interval, byDay, count, until);
    }

    private static Map<String, String> split(String text, Notification notification) {
        Map<String, String> parts = new LinkedHashMap<>();
        for (String piece : text.trim().split(";")) {
            if (piece.isBlank()) continue;
            int equals = piece.indexOf('=');
            if (equals < 1) {
                notification.addError("malformed recurrence part \"" + piece + "\", expected KEY=VALUE");
                continue;
            }
            parts.put(piece.substring(0, equals).trim().toUpperCase(), piece.substring(equals + 1).trim());
        }
        return parts;
    }

    private static Frequency parseFrequency(String value, Notification notification) {
        if (value == null) {
            notification.addError("recurrence FREQ is required");
            return null;
        }
        try {
            return Frequency.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            notification.addError(
                    "unsupported recurrence FREQ \"" + value + "\", expected DAILY, WEEKLY or MONTHLY");
            return null;
        }
    }

    private static int parseInterval(String value, Notification notification) {
        if (value == null) return 1;
        Integer parsed = parseNumber(value, "INTERVAL", notification);
        return parsed == null ? 1 : parsed;
    }

    private static Set<DayOfWeek> parseByDay(String value, Notification notification) {
        if (value == null || value.isBlank()) return Set.of();
        Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        for (String code : value.split(",")) {
            DayOfWeek day = DAY_BY_CODE.get(code.trim().toUpperCase());
            if (day == null) notification.addError("unsupported recurrence BYDAY \"" + code.trim() + "\"");
            else days.add(day);
        }
        return days;
    }

    private static Integer parseNumber(String value, String part, Notification notification) {
        if (value == null) return null;
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            notification.addError("recurrence " + part + " must be a number");
            return null;
        }
    }

    /**
     * Aceita {@code yyyyMMdd} e a forma completa {@code yyyyMMddTHHmmssZ} que as bibliotecas de
     * calendário costumam emitir. Só a data importa: o horário de cada ocorrência vem do evento,
     * não da regra.
     */
    private static LocalDate parseUntil(String value, Notification notification) {
        if (value == null) return null;
        try {
            return LocalDate.parse(
                    value.length() > 8 ? value.substring(0, 8) : value, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException ex) {
            notification.addError("recurrence UNTIL must be a date like 20260930");
            return null;
        }
    }

    /** Forma canônica — é ela que fica persistida, não o texto original informado. */
    public String format() {
        StringBuilder text = new StringBuilder("FREQ=").append(frequency);
        if (interval != 1) text.append(";INTERVAL=").append(interval);
        if (!byDay.isEmpty()) {
            text.append(";BYDAY=");
            text.append(byDay.stream().sorted().map(CODE_BY_DAY::get).reduce((a, b) -> a + "," + b).orElseThrow());
        }
        if (count != null) text.append(";COUNT=").append(count);
        if (until != null) text.append(";UNTIL=").append(until.format(DateTimeFormatter.BASIC_ISO_DATE));
        return text.toString();
    }
}
