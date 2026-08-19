package com.neoguara.rooms.event.domain.services;

import com.neoguara.rooms.event.domain.valueobjects.RecurrenceRule;
import com.neoguara.rooms.shared.domain.validation.Notification;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Materializa uma regra de recorrência nas datas que ela descreve.
 *
 * <p>Tudo aqui anda em {@link LocalDateTime} somando <em>unidades de calendário</em>
 * ({@code plusDays}, {@code plusWeeks}, {@code plusMonths}) e nunca horas. Somar 7×24h atravessa a
 * virada do horário de verão errado, e "toda terça às 14h" viraria 13h no meio da série.
 */
public final class RecurrenceExpander {

    private RecurrenceExpander() {}

    /** Uma ocorrência materializada da série. */
    public record Occurrence(LocalDateTime startAt, LocalDateTime endAt) {}

    /**
     * As ocorrências da regra a partir do primeiro intervalo, ele inclusive. A duração de todas é a
     * do primeiro — mudar a duração no meio da série não é recorrência, é outro evento.
     */
    public static List<Occurrence> expand(RecurrenceRule rule, LocalDateTime startAt, LocalDateTime endAt) {
        Notification.create()
                .addErrorIf(rule == null, "recurrence rule is required")
                .addErrorIf(startAt == null, "startAt is required")
                .addErrorIf(endAt == null, "endAt is required")
                .raiseIfHasErrors();

        Duration duration = Duration.between(startAt, endAt);
        List<LocalDateTime> starts = switch (rule.frequency()) {
            case DAILY -> everyNth(rule, startAt, Step.DAYS);
            case MONTHLY -> everyNth(rule, startAt, Step.MONTHS);
            case WEEKLY -> rule.byDay().isEmpty()
                    ? everyNth(rule, startAt, Step.WEEKS)
                    : weeklyByDay(rule, startAt);
        };

        return starts.stream().map(start -> new Occurrence(start, start.plus(duration))).toList();
    }

    private enum Step {
        DAYS, WEEKS, MONTHS;

        /**
         * Sempre a partir do início original, nunca da ocorrência anterior: mês a mês, o clamp de
         * dia 31 acumularia deriva e 31/01 viraria 28/02, depois 28/03 em vez de 31/03.
         */
        LocalDateTime from(LocalDateTime origin, int amount) {
            return switch (this) {
                case DAYS -> origin.plusDays(amount);
                case WEEKS -> origin.plusWeeks(amount);
                case MONTHS -> origin.plusMonths(amount);
            };
        }
    }

    private static List<LocalDateTime> everyNth(RecurrenceRule rule, LocalDateTime startAt, Step step) {
        List<LocalDateTime> starts = new ArrayList<>();
        for (int index = 0; starts.size() < limitOf(rule); index++) {
            LocalDateTime candidate = step.from(startAt, index * rule.interval());
            if (isAfterUntil(rule, candidate)) break;
            starts.add(candidate);
            raiseIfTooMany(starts);
        }
        return starts;
    }

    /**
     * Percorre semana a semana respeitando o INTERVAL, emitindo os dias de BYDAY na ordem da semana.
     * Descarta o que cai antes do início, porque o BYDAY pode citar um dia que já passou dentro da
     * primeira semana.
     */
    private static List<LocalDateTime> weeklyByDay(RecurrenceRule rule, LocalDateTime startAt) {
        List<LocalDateTime> starts = new ArrayList<>();
        LocalDate firstMonday = startAt.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<DayOfWeek> days = rule.byDay().stream().sorted().toList();

        for (int week = 0; starts.size() < limitOf(rule); week++) {
            LocalDate weekStart = firstMonday.plusWeeks((long) week * rule.interval());
            if (rule.until() != null && weekStart.isAfter(rule.until())) break;

            for (DayOfWeek day : days) {
                LocalDateTime candidate = weekStart
                        .with(TemporalAdjusters.nextOrSame(day))
                        .atTime(startAt.toLocalTime());

                if (candidate.isBefore(startAt)) continue;
                if (isAfterUntil(rule, candidate)) break;

                starts.add(candidate);
                raiseIfTooMany(starts);
                if (starts.size() == limitOf(rule)) break;
            }
        }
        return starts;
    }

    /**
     * COUNT limita direto. Com UNTIL não há limite de contagem — quem barra série grande demais é
     * {@link #raiseIfTooMany}, que recusa em vez de truncar em silêncio.
     */
    private static int limitOf(RecurrenceRule rule) {
        return rule.count() != null ? rule.count() : Integer.MAX_VALUE;
    }

    private static boolean isAfterUntil(RecurrenceRule rule, LocalDateTime candidate) {
        return rule.until() != null && candidate.toLocalDate().isAfter(rule.until());
    }

    private static void raiseIfTooMany(List<LocalDateTime> starts) {
        if (starts.size() > RecurrenceRule.MAX_OCCURRENCES)
            Notification.create()
                    .addError("recurrence expands to more than " + RecurrenceRule.MAX_OCCURRENCES
                            + " occurrences, narrow UNTIL or use COUNT")
                    .raiseIfHasErrors();
    }
}
