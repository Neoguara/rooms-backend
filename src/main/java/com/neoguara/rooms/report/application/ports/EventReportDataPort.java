package com.neoguara.rooms.report.application.ports;

import com.neoguara.rooms.report.application.dtos.EventReportRow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Leitura da agenda para fins de relatório, implementada pelo módulo {@code event}. Devolve
 * {@link EventReportRow}, que é um tipo do módulo {@code report}: se devolvesse a entidade
 * {@code Event}, qualquer refactor nela quebraria o relatório.
 */
public interface EventReportDataPort {

    /** Eventos que tocam o intervalo, em ordem cronológica. Intervalo semiaberto, como no resto do sistema. */
    List<EventReportRow> findForReport(LocalDateTime startAt, LocalDateTime endAt);

    /** Mesma leitura, restrita a uma sala. */
    List<EventReportRow> findForReportByRoom(UUID roomId, LocalDateTime startAt, LocalDateTime endAt);
}
