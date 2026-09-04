package com.neoguara.rooms.report.infrastructure.generators;

import com.neoguara.rooms.report.application.dtos.EventReportRow;
import com.neoguara.rooms.report.application.ports.EventReportDataPort;
import com.neoguara.rooms.report.application.ports.RoomReportDataPort;
import com.neoguara.rooms.report.domain.enums.ReportFormat;
import com.neoguara.rooms.report.domain.enums.ReportType;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvEventsByPeriodGeneratorTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 1, 8, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 9, 30, 18, 0);
    private static final UUID ROOM = UUID.randomUUID();

    @Test
    void generatorAnswersOnlyForItsOwnTypeAndFormat() {
        CsvEventsByPeriodGenerator generator = generatorFor(new RecordingEventData(List.of()), labels(Map.of()));

        assertTrue(generator.supports(ReportType.EVENTS_BY_PERIOD, ReportFormat.CSV));
        assertFalse(generator.supports(ReportType.EVENTS_BY_PERIOD, ReportFormat.PDF));
    }

    @Test
    void roomIdIsReplacedByItsLabel() {
        String csv = generate(
                new RecordingEventData(List.of(row("Reunião anual", ROOM))),
                labels(Map.of(ROOM, "Sala 302 (S302) — Prédio B")),
                ReportParameters.of(START, END, null));

        assertTrue(csv.contains("Reunião anual;Sala 302 (S302) — Prédio B;01/09/2026 08:00;01/09/2026 09:00"),
                csv);
    }

    /** Sala apagada depois do evento não deve derrubar o relatório inteiro — o id ainda identifica. */
    @Test
    void unknownRoomFallsBackToItsId() {
        String csv = generate(
                new RecordingEventData(List.of(row("Reunião anual", ROOM))),
                labels(Map.of()),
                ReportParameters.of(START, END, null));

        assertTrue(csv.contains(ROOM.toString()), csv);
    }

    @Test
    void emptyPeriodProducesAHeaderOnlyFile() {
        String csv = generate(
                new RecordingEventData(List.of()),
                labels(Map.of()),
                ReportParameters.of(START, END, null));

        assertEquals("Título;Sala;Início;Fim;Dia inteiro;Status;Série\r\n", csv);
    }

    @Test
    void parametersWithoutRoomAskForTheWholeAgenda() {
        RecordingEventData eventData = new RecordingEventData(List.of());

        generate(eventData, labels(Map.of()), ReportParameters.of(START, END, null));

        assertTrue(eventData.wholeAgendaQueried);
        assertNull(eventData.roomQueried);
    }

    @Test
    void parametersWithRoomAskOnlyForThatRoom() {
        RecordingEventData eventData = new RecordingEventData(List.of());

        generate(eventData, labels(Map.of()), ReportParameters.of(START, END, ROOM));

        assertEquals(ROOM, eventData.roomQueried);
        assertFalse(eventData.wholeAgendaQueried);
    }

    @Test
    void eventWithoutSeriesLeavesTheColumnEmpty() {
        String csv = generate(
                new RecordingEventData(List.of(row("Reunião anual", ROOM))),
                labels(Map.of(ROOM, "Sala 302")),
                ReportParameters.of(START, END, null));

        assertTrue(csv.trim().endsWith("Não;ACTIVE;"), csv);
    }

    private static String generate(
            EventReportDataPort eventData,
            RoomReportDataPort roomData,
            ReportParameters parameters
    ) {
        return new String(generatorFor(eventData, roomData).generate(parameters), StandardCharsets.UTF_8);
    }

    private static CsvEventsByPeriodGenerator generatorFor(EventReportDataPort eventData, RoomReportDataPort roomData) {
        return new CsvEventsByPeriodGenerator(eventData, roomData, ';', false);
    }

    private static RoomReportDataPort labels(Map<UUID, String> byId) {
        return roomIds -> byId;
    }

    private static EventReportRow row(String title, UUID roomId) {
        return new EventReportRow(
                UUID.randomUUID(), roomId, title,
                LocalDateTime.of(2026, 9, 1, 8, 0),
                LocalDateTime.of(2026, 9, 1, 9, 0),
                false, "ACTIVE", null);
    }

    /** Registra qual das duas consultas foi usada, que é a decisão que {@code roomId} governa. */
    private static final class RecordingEventData implements EventReportDataPort {

        private final List<EventReportRow> rows;
        private UUID roomQueried;
        private boolean wholeAgendaQueried;

        private RecordingEventData(List<EventReportRow> rows) {
            this.rows = rows;
        }

        @Override
        public List<EventReportRow> findForReport(LocalDateTime startAt, LocalDateTime endAt) {
            this.wholeAgendaQueried = true;
            return rows;
        }

        @Override
        public List<EventReportRow> findForReportByRoom(UUID roomId, LocalDateTime startAt, LocalDateTime endAt) {
            this.roomQueried = roomId;
            return rows;
        }
    }
}
