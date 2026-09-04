package com.neoguara.rooms.report.application.ports;

import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.enums.ReportStatus;
import com.neoguara.rooms.report.domain.valueobjects.ReportId;

import java.util.List;
import java.util.Optional;

public interface ReportRepositoryPort {
    Report save(Report report);
    Optional<Report> findById(ReportId id);

    /**
     * Todos os relatórios, do mais recente para o mais antigo. A ordem faz parte do contrato: numa
     * fila de trabalho, o que acabou de ser pedido é o que interessa, e uma lista sem ordenação
     * definida embaralharia isso a cada consulta.
     */
    List<Report> findAll();

    /** Serve à varredura de órfãos no boot. Ver {@code OrphanedReportReconciler}. */
    List<Report> findByStatus(ReportStatus status);
}
