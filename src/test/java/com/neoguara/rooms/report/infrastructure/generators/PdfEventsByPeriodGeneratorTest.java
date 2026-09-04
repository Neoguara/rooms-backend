package com.neoguara.rooms.report.infrastructure.generators;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfEventsByPeriodGeneratorTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 1, 8, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 9, 30, 18, 0);
    private static final UUID ROOM = UUID.randomUUID();
    private static final UUID SERIES = UUID.randomUUID();

    @Test
    void generatorAnswersOnlyForItsOwnTypeAndFormat() {
        PdfEventsByPeriodGenerator generator = generatorFor(new FakeEventData(List.of()), labels(Map.of()));

        assertTrue(generator.supports(ReportType.EVENTS_BY_PERIOD, ReportFormat.PDF));
        assertFalse(generator.supports(ReportType.EVENTS_BY_PERIOD, ReportFormat.CSV));
    }

    @Test
    void producesAValidPdfDocument() {
        byte[] pdf = generate(new FakeEventData(List.of(row("Reunião anual", ROOM, SERIES))),
                labels(Map.of(ROOM, "Sala 302 (S302) — Prédio B")),
                ReportParameters.of(START, END, null));

        assertTrue(new String(pdf, 0, 5, StandardCharsets.ISO_8859_1).equals("%PDF-"),
                "não começa com a assinatura de PDF");
        assertEquals(1, pageCountOf(pdf));
    }

    /**
     * A razão de as fontes serem declaradas com WINANSI. Na codificação padrão os acentos saem
     * corrompidos, e o defeito só aparece ao abrir o documento — nunca na geração.
     */
    @Test
    void accentedTextSurvivesTheEncoding() {
        String text = textOf(generate(
                new FakeEventData(List.of(row("Reunião de avaliação", ROOM, null))),
                labels(Map.of(ROOM, "Laboratório 1032")),
                ReportParameters.of(START, END, null)));

        assertTrue(text.contains("Eventos por período"), text);
        assertTrue(text.contains("Título"), text);
        assertTrue(text.contains("Início"), text);
        assertTrue(text.contains("Reunião de avaliação"), text);
        assertTrue(text.contains("Laboratório 1032"), text);
    }

    /** É o que justifica a página em paisagem: identificador truncado convida a copiar valor inválido. */
    @Test
    void seriesIdIsPrintedInFull() {
        String text = textOf(generate(
                new FakeEventData(List.of(row("Reunião anual", ROOM, SERIES))),
                labels(Map.of(ROOM, "Sala 302")),
                ReportParameters.of(START, END, null)));

        assertTrue(text.contains(SERIES.toString()), text);
    }

    @Test
    void emptyPeriodSaysSoInsteadOfShowingAnEmptyTable() {
        String text = textOf(generate(new FakeEventData(List.of()), labels(Map.of()),
                ReportParameters.of(START, END, null)));

        assertTrue(text.contains("Nenhum evento no período"), text);
        assertFalse(text.contains("Título"), text);
    }

    @Test
    void roomScopedReportNamesTheRoomInTheHeading() {
        String text = textOf(generate(
                new FakeEventData(List.of(row("Reunião anual", ROOM, null))),
                labels(Map.of(ROOM, "Sala 302 (S302) — Prédio B")),
                ReportParameters.of(START, END, ROOM)));

        assertTrue(text.contains("Sala: Sala 302 (S302)"), text);
    }

    @Test
    void reportWithoutRoomFilterSaysItCoversEveryRoom() {
        String text = textOf(generate(new FakeEventData(List.of()), labels(Map.of()),
                ReportParameters.of(START, END, null)));

        assertTrue(text.contains("Salas: todas"), text);
    }

    private static byte[] generate(
            EventReportDataPort eventData,
            RoomReportDataPort roomData,
            ReportParameters parameters
    ) {
        return generatorFor(eventData, roomData).generate(parameters);
    }

    private static PdfEventsByPeriodGenerator generatorFor(EventReportDataPort eventData, RoomReportDataPort roomData) {
        return new PdfEventsByPeriodGenerator(eventData, roomData);
    }

    private static String textOf(byte[] pdf) {
        try {
            PdfReader reader = new PdfReader(pdf);
            StringBuilder text = new StringBuilder();
            PdfTextExtractor extractor = new PdfTextExtractor(reader);
            for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                text.append(extractor.getTextFromPage(page)).append('\n');
            }
            reader.close();
            return text.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível ler o PDF gerado", e);
        }
    }

    private static int pageCountOf(byte[] pdf) {
        try {
            PdfReader reader = new PdfReader(pdf);
            int pages = reader.getNumberOfPages();
            reader.close();
            return pages;
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível ler o PDF gerado", e);
        }
    }

    private static RoomReportDataPort labels(Map<UUID, String> byId) {
        return roomIds -> byId;
    }

    private static EventReportRow row(String title, UUID roomId, UUID seriesId) {
        return new EventReportRow(
                UUID.randomUUID(), roomId, title,
                LocalDateTime.of(2026, 9, 1, 8, 0),
                LocalDateTime.of(2026, 9, 1, 9, 0),
                false, "ACTIVE", seriesId);
    }

    private static final class FakeEventData implements EventReportDataPort {

        private final List<EventReportRow> rows;

        private FakeEventData(List<EventReportRow> rows) {
            this.rows = rows;
        }

        @Override
        public List<EventReportRow> findForReport(LocalDateTime startAt, LocalDateTime endAt) {
            return rows;
        }

        @Override
        public List<EventReportRow> findForReportByRoom(UUID roomId, LocalDateTime startAt, LocalDateTime endAt) {
            return rows;
        }
    }
}
