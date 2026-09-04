package com.neoguara.rooms.report.application.mappers;

import com.neoguara.rooms.report.application.dtos.ReportResponse;
import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.enums.ReportStatus;
import com.neoguara.rooms.report.domain.valueobjects.ReportArtifact;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;

public class ReportMapper {

    private ReportMapper() {}

    public static ReportResponse toResponse(Report report) {
        ReportParameters parameters = report.getParameters();
        ReportArtifact artifact = report.getArtifact();
        boolean downloadable = report.getStatus() == ReportStatus.COMPLETED && artifact != null;

        return new ReportResponse(
                report.getId().id(),
                report.getRequestedBy().id(),
                report.getType().name(),
                report.getFormat().name(),
                report.getStatus().name(),
                parameters.startAt(),
                parameters.endAt(),
                parameters.roomId(),
                report.getRequestedAt(),
                report.getStartedAt(),
                report.getFinishedAt(),
                report.getFailureReason(),
                artifact != null ? artifact.filename() : null,
                artifact != null ? artifact.sizeBytes() : null,
                // Só existe link quando há arquivo do outro lado: um downloadUrl preenchido em
                // relatório pendente convidaria o cliente a chamar um endpoint que responde 422.
                downloadable ? downloadUrl(report) : null
        );
    }

    private static String downloadUrl(Report report) {
        return "/reports/" + report.getId().id() + "/download";
    }
}
