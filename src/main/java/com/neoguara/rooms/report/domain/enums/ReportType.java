package com.neoguara.rooms.report.domain.enums;

/**
 * O que o relatório apura. O prefixo de arquivo vive junto porque é a face pública do tipo: é o
 * nome que aparece na pasta de downloads de quem baixou.
 */
public enum ReportType {
    /** Agenda de eventos de um intervalo, opcionalmente restrita a uma sala. */
    EVENTS_BY_PERIOD("eventos-por-periodo");

    private final String filenamePrefix;

    ReportType(String filenamePrefix) {
        this.filenamePrefix = filenamePrefix;
    }

    public String filenamePrefix() {
        return filenamePrefix;
    }
}
