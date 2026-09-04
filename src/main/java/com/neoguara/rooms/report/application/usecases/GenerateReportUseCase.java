package com.neoguara.rooms.report.application.usecases;

import com.neoguara.rooms.report.application.ports.ReportStoragePort;
import com.neoguara.rooms.report.application.services.ReportGeneratorRegistry;
import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.valueobjects.ReportArtifact;
import com.neoguara.rooms.report.domain.valueobjects.ReportId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Produz o arquivo de um relatório já registrado. Roda fora da requisição HTTP, acordado pelo
 * listener — ninguém está esperando a resposta, então toda falha precisa terminar gravada no
 * próprio relatório em vez de subir para um chamador que não existe.
 */
@Service
public class GenerateReportUseCase {

    /**
     * Único logger do projeto, e de propósito. Aqui não há requisição para carregar o erro de
     * volta: {@code failureReason} guarda a mensagem truncada, e sem o log a pilha de execução —
     * a parte que diz onde quebrou — se perderia por completo.
     */
    private static final Logger log = LoggerFactory.getLogger(GenerateReportUseCase.class);

    private final ReportStateWriter stateWriter;
    private final ReportGeneratorRegistry generators;
    private final ReportStoragePort storage;

    GenerateReportUseCase(
            ReportStateWriter stateWriter,
            ReportGeneratorRegistry generators,
            ReportStoragePort storage
    ) {
        this.stateWriter = stateWriter;
        this.generators = generators;
        this.storage = storage;
    }

    public void execute(UUID reportId) {
        ReportId id = ReportId.of(reportId);

        // Fora do try de propósito: se o relatório sumiu ou já saiu de PENDING, não há o que
        // marcar como falha — marcar assim mesmo sobrescreveria o resultado de quem o processou
        // de verdade.
        Report report = stateWriter.markProcessing(id);

        try {
            byte[] content = generators
                    .require(report.getType(), report.getFormat())
                    .generate(report.getParameters());

            String filename = filenameFor(report);
            String storageKey = storage.store(id, filename, content);

            stateWriter.markCompleted(id, ReportArtifact.of(
                    filename,
                    report.getFormat().contentType(),
                    content.length,
                    storageKey
            ));
        } catch (RuntimeException e) {
            log.error("Report generation failed for report {}", reportId, e);
            stateWriter.markFailed(id, describe(e));
        }
    }

    private static String filenameFor(Report report) {
        return "%s-%s.%s".formatted(
                report.getType().filenamePrefix(),
                report.getRequestedAt().toLocalDate(),
                report.getFormat().extension()
        );
    }

    /** Exceções sem mensagem — {@code NullPointerException} à frente — deixariam o motivo em branco. */
    private static String describe(RuntimeException e) {
        return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
    }
}
