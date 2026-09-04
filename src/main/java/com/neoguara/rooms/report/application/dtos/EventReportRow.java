package com.neoguara.rooms.report.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Uma linha da agenda como o relatório precisa dela. Pertence ao módulo {@code report}, e não a
 * {@code event}, para que o formato do arquivo não fique amarrado à entidade: o módulo {@code event}
 * é livre para mudar {@code Event} desde que continue sabendo preencher isto.
 *
 * <p>{@code status} vem como texto pelo mesmo motivo — importar {@code EventStatus} traria o enum
 * de outro módulo para dentro do arquivo gerado.
 */
public record EventReportRow(
        UUID eventId,
        UUID roomId,
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Boolean allDay,
        String status,
        UUID seriesId
) {}
