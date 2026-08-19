package com.neoguara.rooms.event.domain.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * O quanto uma ocorrência de referência andou, para que as irmãs andem igual.
 *
 * <p>Guarda <strong>dias de calendário</strong> e horário do dia, e não uma duração corrida. A
 * diferença aparece na virada do horário de verão: "um dia depois, no mesmo horário" não é o mesmo
 * que "24 horas depois", e é a primeira leitura que descreve o que alguém quer ao mover uma série
 * de terça para quarta.
 *
 * <p>A duração, essa sim, é corrida — uma reunião de duas horas dura duas horas.
 */
public record OccurrenceShift(long days, LocalTime timeOfDay, Duration duration) {

    /**
     * O deslocamento que leva {@code referenceStart} até o intervalo pretendido.
     */
    public static OccurrenceShift between(
            LocalDateTime referenceStart, LocalDateTime newStart, LocalDateTime newEnd) {
        return new OccurrenceShift(
                ChronoUnit.DAYS.between(referenceStart.toLocalDate(), newStart.toLocalDate()),
                newStart.toLocalTime(),
                Duration.between(newStart, newEnd));
    }

    public LocalDateTime startFrom(LocalDateTime occurrenceStart) {
        return occurrenceStart.toLocalDate().plusDays(days).atTime(timeOfDay);
    }

    public LocalDateTime endFrom(LocalDateTime occurrenceStart) {
        return startFrom(occurrenceStart).plus(duration);
    }
}
