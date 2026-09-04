package com.neoguara.rooms.report.application.usecases;

import com.neoguara.rooms.report.application.ports.ReportRepositoryPort;
import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.valueobjects.ReportArtifact;
import com.neoguara.rooms.report.domain.valueobjects.ReportId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Grava as transições de estado do relatório, cada uma na própria transação.
 *
 * <p>É um bean separado de {@link GenerateReportUseCase} por necessidade, não por gosto: anotar
 * métodos da própria classe com {@code @Transactional} e chamá-los de dentro dela não passa pelo
 * proxy do Spring, e a anotação viraria decoração silenciosa. Só a chamada entre beans distintos
 * cria transação de verdade.
 *
 * <p>{@code REQUIRES_NEW} garante que marcar {@code FAILED} seja gravado mesmo quando a transação
 * de quem chamou for descartada — sem isso, a falha desapareceria junto com o rollback e o
 * relatório ficaria preso em {@code PROCESSING}.
 */
@Service
public class ReportStateWriter {

    private final ReportRepositoryPort repository;

    ReportStateWriter(ReportRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Report markProcessing(ReportId id) {
        Report report = load(id);
        report.start();
        return repository.save(report);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompleted(ReportId id, ReportArtifact artifact) {
        Report report = load(id);
        report.complete(artifact);
        repository.save(report);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(ReportId id, String reason) {
        Report report = load(id);
        report.fail(reason);
        repository.save(report);
    }

    private Report load(ReportId id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", id.id()));
    }
}
