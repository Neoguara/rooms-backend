package com.neoguara.rooms.report.infrastructure.listeners;

import com.neoguara.rooms.report.application.events.ReportRequested;
import com.neoguara.rooms.report.application.usecases.GenerateReportUseCase;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Tira o relatório da fila e manda gerar.
 *
 * <p>{@code @ApplicationModuleListener} reúne três coisas de uma vez: {@code @Async}, para que a
 * requisição HTTP já tenha respondido; entrega só depois do commit, para que o worker encontre o
 * relatório gravado; e transação própria. Com {@code spring-modulith-starter-jpa} no classpath, a
 * publicação fica registrada no banco até este método terminar — é isso que permite reprocessar o
 * que ficou pendente se a aplicação cair no meio da geração.
 */
@Component
public class ReportRequestedListener {

    private final GenerateReportUseCase generateReportUseCase;

    ReportRequestedListener(GenerateReportUseCase generateReportUseCase) {
        this.generateReportUseCase = generateReportUseCase;
    }

    @ApplicationModuleListener
    public void on(ReportRequested event) {
        generateReportUseCase.execute(event.reportId());
    }
}
