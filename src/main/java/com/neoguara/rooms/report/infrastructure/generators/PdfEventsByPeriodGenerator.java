package com.neoguara.rooms.report.infrastructure.generators;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.neoguara.rooms.report.application.ports.EventReportDataPort;
import com.neoguara.rooms.report.application.ports.ReportGenerator;
import com.neoguara.rooms.report.application.ports.RoomReportDataPort;
import com.neoguara.rooms.report.domain.enums.ReportFormat;
import com.neoguara.rooms.report.domain.enums.ReportType;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Agenda do intervalo em PDF, com as mesmas colunas do CSV — as duas saídas leem
 * {@link EventsByPeriodTable}, então não podem divergir.
 *
 * <p>Página em paisagem porque são sete colunas e uma delas carrega o UUID da série por inteiro.
 * Em retrato, ou o identificador seria truncado — e identificador truncado é pior que ausente, já
 * que convida a copiar um valor que não funciona — ou as demais colunas ficariam ilegíveis.
 *
 * <p>As fontes são declaradas com {@code WINANSI}: na codificação padrão os acentos do português
 * saem corrompidos no documento.
 */
@Component
public class PdfEventsByPeriodGenerator implements ReportGenerator {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, BaseFont.WINANSI, 14);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.WINANSI, 9);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, BaseFont.WINANSI, 8);
    private static final Font BODY_FONT = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.WINANSI, 7.5f);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.WINANSI, 7);

    private static final Color HEADER_BACKGROUND = new Color(0xEE, 0xEE, 0xEE);

    /** Proporções entre as colunas de {@link EventsByPeriodTable#HEADERS}, na mesma ordem. */
    private static final float[] COLUMN_WIDTHS = {3.2f, 3.2f, 1.6f, 1.6f, 1.0f, 1.3f, 3.0f};

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final EventReportDataPort eventData;
    private final RoomReportDataPort roomData;

    PdfEventsByPeriodGenerator(EventReportDataPort eventData, RoomReportDataPort roomData) {
        this.eventData = eventData;
        this.roomData = roomData;
    }

    @Override
    public boolean supports(ReportType type, ReportFormat format) {
        return type == ReportType.EVENTS_BY_PERIOD && format == ReportFormat.PDF;
    }

    @Override
    public byte[] generate(ReportParameters parameters) {
        List<List<String>> rows = EventsByPeriodTable.rows(eventData, roomData, parameters);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 40, 44);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new PageFooter());

            document.open();
            document.add(heading(parameters, rows));
            // Ao contrário do CSV, que sai só com cabeçalho, aqui vale a frase: uma tabela vazia
            // num documento para leitura passa impressão de arquivo quebrado.
            document.add(rows.isEmpty()
                    ? new Paragraph("Nenhum evento no período.", BODY_FONT)
                    : table(rows));
            document.close();
        } catch (DocumentException e) {
            throw new IllegalStateException("Could not render the PDF report", e);
        }

        return out.toByteArray();
    }

    private static Element heading(ReportParameters parameters, List<List<String>> rows) {
        Paragraph heading = new Paragraph();
        heading.add(new Paragraph("Eventos por período", TITLE_FONT));
        heading.add(new Paragraph("Período: %s a %s".formatted(
                TIMESTAMP.format(parameters.startAt()),
                TIMESTAMP.format(parameters.endAt())), SUBTITLE_FONT));
        heading.add(new Paragraph(scope(parameters, rows), SUBTITLE_FONT));
        heading.add(new Paragraph("Emitido em " + TIMESTAMP.format(LocalDateTime.now()), SUBTITLE_FONT));
        heading.setSpacingAfter(14f);
        return heading;
    }

    /**
     * O rótulo da sala é reaproveitado da primeira linha em vez de consultado outra vez — num
     * relatório restrito a uma sala, todas as linhas trazem a mesma.
     */
    private static String scope(ReportParameters parameters, List<List<String>> rows) {
        if (!parameters.isRoomScoped()) return "Salas: todas";
        return "Sala: " + (rows.isEmpty() ? parameters.roomId().toString() : rows.getFirst().get(1));
    }

    private static Element table(List<List<String>> rows) {
        PdfPTable table = new PdfPTable(COLUMN_WIDTHS);
        table.setWidthPercentage(100);
        // Repete o cabeçalho em toda página: sem isto, da segunda em diante não dá para saber que
        // coluna é qual.
        table.setHeaderRows(1);

        for (String header : EventsByPeriodTable.HEADERS) {
            table.addCell(cell(header, HEADER_FONT, HEADER_BACKGROUND));
        }
        for (List<String> row : rows) {
            for (String value : row) {
                table.addCell(cell(value, BODY_FONT, null));
            }
        }
        return table;
    }

    private static PdfPCell cell(String value, Font font, Color background) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setPadding(4f);
        if (background != null) cell.setBackgroundColor(background);
        return cell;
    }

    /** Numeração de página, escrita ao fim de cada uma — só o writer sabe em qual está. */
    private static final class PageFooter extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(
                    writer.getDirectContent(),
                    Element.ALIGN_RIGHT,
                    new Phrase("Página " + writer.getPageNumber(), FOOTER_FONT),
                    document.right(),
                    document.bottom() - 20,
                    0);
        }
    }
}
