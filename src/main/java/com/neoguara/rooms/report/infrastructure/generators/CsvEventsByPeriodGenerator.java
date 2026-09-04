package com.neoguara.rooms.report.infrastructure.generators;

import com.neoguara.rooms.report.application.ports.EventReportDataPort;
import com.neoguara.rooms.report.application.ports.ReportGenerator;
import com.neoguara.rooms.report.application.ports.RoomReportDataPort;
import com.neoguara.rooms.report.domain.enums.ReportFormat;
import com.neoguara.rooms.report.domain.enums.ReportType;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/** Agenda do intervalo em CSV. As colunas vêm de {@link EventsByPeriodTable}. */
@Component
public class CsvEventsByPeriodGenerator implements ReportGenerator {

    private final EventReportDataPort eventData;
    private final RoomReportDataPort roomData;
    private final char separator;
    private final boolean withBom;

    CsvEventsByPeriodGenerator(
            EventReportDataPort eventData,
            RoomReportDataPort roomData,
            @Value("${report.csv.separator:;}") char separator,
            @Value("${report.csv.bom:true}") boolean withBom
    ) {
        this.eventData = eventData;
        this.roomData = roomData;
        this.separator = separator;
        this.withBom = withBom;
    }

    @Override
    public boolean supports(ReportType type, ReportFormat format) {
        return type == ReportType.EVENTS_BY_PERIOD && format == ReportFormat.CSV;
    }

    @Override
    public byte[] generate(ReportParameters parameters) {
        List<List<String>> rows = EventsByPeriodTable.rows(eventData, roomData, parameters);

        // Intervalo sem nenhum evento gera arquivo só com cabeçalho, e não uma falha: não achar
        // nada é uma resposta legítima da agenda.
        return CsvWriter.write(EventsByPeriodTable.HEADERS, rows, separator, withBom);
    }
}
