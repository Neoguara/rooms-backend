package com.neoguara.rooms.report.application.usecases;

import com.neoguara.rooms.report.application.ports.ReportRepositoryPort;
import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.enums.ReportFormat;
import com.neoguara.rooms.report.domain.enums.ReportStatus;
import com.neoguara.rooms.report.domain.enums.ReportType;
import com.neoguara.rooms.report.domain.valueobjects.ReportId;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;
import com.neoguara.rooms.report.domain.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReconcileOrphanedReportsUseCaseTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 1, 8, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 9, 30, 18, 0);

    /**
     * O caso que motivou a varredura: a aplicação caiu com o relatório em PROCESSING, e sem isto
     * ele ficaria assim para sempre — o republish não consegue retomá-lo, porque start() recusa.
     */
    @Test
    void reportLeftProcessingByAPreviousRunIsFailed() {
        StubReports reports = new StubReports();
        Report orphan = reports.persistProcessing();

        int reconciled = new ReconcileOrphanedReportsUseCase(reports).execute(LocalDateTime.now().plusHours(1));

        assertEquals(1, reconciled);
        assertEquals(ReportStatus.FAILED, orphan.getStatus());
        assertEquals("Interrupted by application shutdown", orphan.getFailureReason());
    }

    /**
     * O corte por startedAt existe para isto: um relatório que o boot atual acabou de começar a
     * gerar está vivo, e marcá-lo como interrompido destruiria trabalho em andamento.
     */
    @Test
    void reportStartedByTheCurrentRunIsLeftAlone() {
        StubReports reports = new StubReports();
        Report running = reports.persistProcessing();

        int reconciled = new ReconcileOrphanedReportsUseCase(reports).execute(LocalDateTime.now().minusHours(1));

        assertEquals(0, reconciled);
        assertEquals(ReportStatus.PROCESSING, running.getStatus());
        assertNull(running.getFailureReason());
    }

    @Test
    void pendingReportsAreNotTouchedBecauseTheRepublishRetakesThem() {
        StubReports reports = new StubReports();
        reports.persistPending();

        assertEquals(0, new ReconcileOrphanedReportsUseCase(reports).execute(LocalDateTime.now().plusHours(1)));
    }

    private static final class StubReports implements ReportRepositoryPort {

        private final List<Report> stored = new ArrayList<>();

        Report persistPending() {
            Report report = Report.request(
                    UserId.of(UUID.randomUUID()),
                    ReportType.EVENTS_BY_PERIOD,
                    ReportFormat.CSV,
                    ReportParameters.of(START, END, null));
            stored.add(report);
            return report;
        }

        Report persistProcessing() {
            Report report = persistPending();
            report.start();
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
