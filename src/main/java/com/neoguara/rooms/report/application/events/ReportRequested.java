package com.neoguara.rooms.report.application.events;

import java.util.UUID;

/**
 * Avisa que há um relatório esperando na fila. Carrega só o id: quando o worker acordar, o estado
 * no banco pode não ser mais o do instante da publicação, então quem processa relê em vez de
 * confiar num retrato antigo.
 *
 * <p>Vive em {@code application} e não em {@code domain} porque não é um fato do negócio de
 * relatórios — é o mecanismo que liga a submissão ao processamento.
 */
public record ReportRequested(UUID reportId) {
}
