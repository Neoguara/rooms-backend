package com.neoguara.rooms.report.application.usecases;

import com.neoguara.rooms.report.application.dtos.ReportResponse;
import com.neoguara.rooms.report.application.ports.ReportRepositoryPort;
import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.enums.ReportFormat;
import com.neoguara.rooms.report.domain.enums.ReportStatus;
import com.neoguara.rooms.report.domain.enums.ReportType;
import com.neoguara.rooms.report.domain.valueobjects.ReportArtifact;
import com.neoguara.rooms.report.domain.valueobjects.ReportId;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;
import com.neoguara.rooms.report.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetReportUseCaseTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 1, 8, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 9, 30, 18, 0);

    private static final UUID ANA = UUID.randomUUID();
    private static final UUID BRUNO = UUID.randomUUID();

    @Test
    void listingIsEmptyWhenNothingWasEverRequested() {
        assertEquals(List.of(), new GetReportUseCase(new StubReports()).findAll());
    }

    /**
     * A listagem mostra a fila inteira, e não só o que o usuário pediu. É diferente de
     * {@code execute}, que trata relatório alheio como inexistente — aqui saem apenas metadados.
     */
    @Test
    void listingIncludesReportsFromEveryUser() {
        StubReports reports = new StubReports();
        reports.persist(ANA);
        reports.persist(BRUNO);

        List<ReportResponse> listed = new GetReportUseCase(reports).findAll();

        assertEquals(2, listed.size());
        assertTrue(listed.stream().anyMatch(r -> r.requestedBy().equals(ANA)));
        assertTrue(listed.stream().anyMatch(r -> r.requestedBy().equals(BRUNO)));
    }

    /** Relatório pendente não tem arquivo do outro lado, então o link não pode ser oferecido. */
    @Test
    void listingOffersDownloadOnlyForCompletedReports() {
        StubReports reports = new StubReports();
        reports.persist(ANA);
        Report done = reports.persist(ANA);
        done.start();
        done.complete(ReportArtifact.of("eventos.csv", "text/csv", 55, "chave.csv"));

        List<ReportResponse> listed = new GetReportUseCase(reports).findAll();

        assertEquals(1, listed.stream().filter(r -> r.downloadUrl() != null).count());
        assertEquals(1, listed.stream().filter(r -> ReportStatus.PENDING.name().equals(r.status())).count());
        listed.stream()
                .filter(r -> ReportStatus.PENDING.name().equals(r.status()))
                .forEach(r -> assertNull(r.downloadUrl()));
    }

    @Test
    void singleReportOfAnotherUserIsNotFound() {
        StubReports reports = new StubReports();
        Report ofAna = reports.persist(ANA);

        GetReportUseCase useCase = new GetReportUseCase(reports);
        UUID id = ofAna.getId().id();

        assertEquals(ANA, useCase.execute(id, ANA).requestedBy());
        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(id, BRUNO));
    }

    @Test
    void unknownReportIsNotFound() {
        GetReportUseCase useCase = new GetReportUseCase(new StubReports());
        UUID absent = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(absent, ANA));
    }

    private static final class StubReports implements ReportRepositoryPort {

        private final List<Report> stored = new ArrayList<>();

        Report persist(UUID requestedBy) {
            Report report = Report.request(
                    UserId.of(requestedBy),
                    ReportType.EVENTS_BY_PERIOD,
                    ReportFormat.CSV,
                    ReportParameters.of(START, END, null));
            stored.add(report);
            return report;
        }

        @Override
        public Report save(Report report) {
            return report;
        }

        @Override
        public Optional<Report> findById(ReportId id) {
            return stored.stream().filter(report -> report.getId().equals(id)).findFirst();
        }

        @Override
        public List<Report> findByStatus(ReportStatus status) {
            return stored.stream().filter(report -> report.getStatus() == status).toList();
        }

        @Override
        public List<Report> findAll() {
            return List.copyOf(stored);
        }
    }
}
