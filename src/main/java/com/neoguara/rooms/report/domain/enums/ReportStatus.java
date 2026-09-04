package com.neoguara.rooms.report.domain.enums;

/**
 * Ciclo de vida do relatório: {@code PENDING} → {@code PROCESSING} → {@code COMPLETED} ou
 * {@code FAILED}. Não há volta atrás — reprocessar é pedir um relatório novo.
 *
 * <p>{@code EXPIRED} já entra aqui embora nada o produza ainda. A coluna gerada pelo H2 é um
 * {@code ENUM(...)} fixado com os valores existentes na criação da tabela, e {@code ddl-auto=update}
 * não amplia esse conjunto sozinho: acrescentar a constante depois exigiria alterar a coluna à mão.
 */
public enum ReportStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    /** Relatório concluído cujo arquivo já foi descartado. Ainda sem produtor: a limpeza é etapa futura. */
    EXPIRED
}
