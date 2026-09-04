package com.neoguara.rooms.report.infrastructure.generators;

import com.neoguara.rooms.report.application.dtos.EventReportRow;
import com.neoguara.rooms.report.application.ports.EventReportDataPort;
import com.neoguara.rooms.report.application.ports.RoomReportDataPort;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Monta as linhas do relatório de eventos por período, já formatadas como texto.
 *
 * <p>É compartilhada entre o gerador de CSV e o de PDF porque os dois mostram exatamente a mesma
 * tabela — só mudam o desenho. Deixar cada um buscar e formatar por conta própria faria as colunas
 * divergirem na primeira alteração que alguém esquecesse de repetir do outro lado.
 *
 * <p>Os cabeçalhos estão em português porque quem lê o arquivo é o consumidor da API, mesma razão
 * pela qual a documentação do springdoc também está.
 */
final class EventsByPeriodTable {

    static final List<String> HEADERS =
            List.of("Título", "Sala", "Início", "Fim", "Dia inteiro", "Status", "Série");

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private EventsByPeriodTable() {}

    static List<List<String>> rows(
            EventReportDataPort eventData,
            RoomReportDataPort roomData,
            ReportParameters parameters
    ) {
        List<EventReportRow> rows = parameters.isRoomScoped()
                ? eventData.findForReportByRoom(parameters.roomId(), parameters.startAt(), parameters.endAt())
                : eventData.findForReport(parameters.startAt(), parameters.endAt());

        // Um lote só para todas as salas do resultado, em vez de uma consulta por linha.
        Map<UUID, String> roomLabels = roomData.resolveRoomLabels(
                rows.stream().map(EventReportRow::roomId).distinct().toList());

        return rows.stream().map(row -> toLine(row, roomLabels)).toList();
    }

    private static List<String> toLine(EventReportRow row, Map<UUID, String> roomLabels) {
        return List.of(
                text(row.title()),
                // Sala apagada depois do evento cai no id: some do mapa, mas a linha ainda precisa
                // identificar onde aquilo aconteceu.
                roomLabels.getOrDefault(row.roomId(), String.valueOf(row.roomId())),
                timestamp(row.startAt()),
                timestamp(row.endAt()),
                Boolean.TRUE.equals(row.allDay()) ? "Sim" : "Não",
                text(row.status()),
                row.seriesId() != null ? row.seriesId().toString() : ""
        );
    }

    private static String timestamp(LocalDateTime value) {
        return value != null ? TIMESTAMP.format(value) : "";
    }

    private static String text(String value) {
        return value != null ? value : "";
    }
}
