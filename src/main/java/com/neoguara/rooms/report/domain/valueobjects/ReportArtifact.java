package com.neoguara.rooms.report.domain.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Ponteiro para o arquivo gerado — o banco guarda o ticket, o conteúdo fica fora dele. Manter os
 * bytes numa coluna faria cada listagem de relatório arrastar megabytes, e o H2 em arquivo é o pior
 * lugar possível para isso.
 *
 * <p>{@code sizeBytes} é {@code Long} e não {@code long}, e não há guarda de nulo no construtor
 * compacto, porque o embeddable é nulo enquanto o relatório não concluiu: com tipo primitivo ou com
 * guarda, carregar um relatório ainda {@code PENDING} quebraria na desserialização. Quem garante que
 * o artefato existe ao concluir é {@link com.neoguara.rooms.report.domain.entities.Report#complete}.
 */
@Embeddable
public record ReportArtifact(String filename, String contentType, Long sizeBytes, String storageKey) {

    public static ReportArtifact of(String filename, String contentType, long sizeBytes, String storageKey) {
        return new ReportArtifact(filename, contentType, sizeBytes, storageKey);
    }
}
