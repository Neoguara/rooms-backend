package com.neoguara.rooms.report.application.usecases;

import com.neoguara.rooms.report.application.dtos.ReportDownload;
import com.neoguara.rooms.report.application.ports.ReportRepositoryPort;
import com.neoguara.rooms.report.application.ports.ReportStoragePort;
import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.valueobjects.ReportArtifact;
import com.neoguara.rooms.report.domain.valueobjects.ReportId;
import com.neoguara.rooms.report.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DownloadReportUseCase {

    private final ReportRepositoryPort repository;
    private final ReportStoragePort storage;

    DownloadReportUseCase(ReportRepositoryPort repository, ReportStoragePort storage) {
        this.repository = repository;
        this.storage = storage;
    }

    public ReportDownload execute(UUID reportId, UUID requesterId) {
        Report report = repository.findById(ReportId.of(reportId))
                .orElseThrow(() -> new ResourceNotFoundException("Report", reportId));

        // Mesmo critério do acompanhamento: relatório alheio não existe para quem pergunta.
        if (!report.isOwnedBy(UserId.of(requesterId)))
            throw new ResourceNotFoundException("Report", reportId);

        // Quem recusa o download de um relatório não concluído é a entidade, não este caso de uso.
        ReportArtifact artifact = report.requireArtifact();

        return new ReportDownload(
                storage.load(artifact.storageKey()),
                artifact.filename(),
                artifact.contentType(),
                artifact.sizeBytes()
        );
    }
}
