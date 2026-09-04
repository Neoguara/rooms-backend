package com.neoguara.rooms.report.domain.enums;

/**
 * Formato de saída. O tipo MIME e a extensão moram aqui porque são a mesma decisão: quem acrescenta
 * um formato não deveria precisar lembrar de atualizar o cabeçalho do download em outro lugar.
 */
public enum ReportFormat {
    CSV("text/csv; charset=UTF-8", "csv"),
    PDF("application/pdf", "pdf");

    private final String contentType;
    private final String extension;

    ReportFormat(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public String contentType() {
        return contentType;
    }

    public String extension() {
        return extension;
    }
}
