package com.neoguara.rooms.report.application.ports;

import com.neoguara.rooms.report.domain.enums.ReportFormat;
import com.neoguara.rooms.report.domain.enums.ReportType;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;

/**
 * Produz o arquivo de um par {@code (tipo, formato)}. Cada implementação busca os próprios dados em
 * vez de recebê-los prontos: os tipos de relatório não compartilham formato de linha, e uma
 * assinatura genérica o bastante para todos acabaria sendo {@code Object}.
 *
 * <p>É o ponto de extensão do módulo — acrescentar um tipo ou um formato é uma classe nova
 * anotada com {@code @Component}, e nada mais.
 */
public interface ReportGenerator {

    boolean supports(ReportType type, ReportFormat format);

    byte[] generate(ReportParameters parameters);
}
