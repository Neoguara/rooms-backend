package com.neoguara.rooms.report.domain.entities;

import com.neoguara.rooms.report.domain.enums.ReportFormat;
import com.neoguara.rooms.report.domain.enums.ReportStatus;
import com.neoguara.rooms.report.domain.enums.ReportType;
import com.neoguara.rooms.report.domain.valueobjects.ReportArtifact;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;
import com.neoguara.rooms.report.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportLifecycleTest {

    private static final UserId REQUESTER = UserId.of(UUID.randomUUID());
    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 1, 8, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 9, 30, 18, 0);

    @Test
    void newReportStartsPending() {
        Report report = pendingReport();

        assertEquals(ReportStatus.PENDING, report.getStatus());
        assertNull(report.getArtifact());
        assertNull(report.getStartedAt());
        assertNull(report.getFinishedAt());
    }

    @Test
    void pendingReportCannotBeDownloaded() {
        Report report = pendingReport();

        InvalidStateException error = assertThrows(InvalidStateException.class, report::requireArtifact);
        assertEquals("Only completed reports can be downloaded", error.getMessage());
    }

    @Test
    void completedReportGivesUpItsArtifact() {
        Report report = pendingReport();
        report.start();
        report.complete(anArtifact());

        assertEquals(ReportStatus.COMPLETED, report.getStatus());
        assertEquals(anArtifact(), report.requireArtifact());
        assertNotNull(report.getFinishedAt());
    }

    @Test
    void reportCannotStartTwice() {
        Report report = pendingReport();
        report.start();

        assertThrows(InvalidStateException.class, report::start);
    }

    @Test
    void completingWithoutArtifactIsRefused() {
        Report report = pendingReport();
        report.start();

        assertThrows(InvalidStateException.class, () -> report.complete(null));
    }

    @Test
    void failedReportKeepsTheFailureReason() {
        Report report = pendingReport();
        report.start();
        report.fail("Room labels could not be resolved");

        assertEquals(ReportStatus.FAILED, report.getStatus());
        assertEquals("Room labels could not be resolved", report.getFailureReason());
        assertThrows(InvalidStateException.class, report::requireArtifact);
    }

    /**
     * O motivo vem de exceções, que não têm tamanho previsível. Recusar a gravação deixaria o
     * relatório preso em PROCESSING, que é bem pior do que guardar a mensagem pela metade.
     */
    @Test
    void failureReasonLongerThanTheColumnIsTruncatedInsteadOfRejected() {
        Report report = pendingReport();
        report.start();

        assertDoesNotThrow(() -> report.fail("x".repeat(600)));
        assertEquals(500, report.getFailureReason().length());
    }

    @Test
    void alreadyFinishedReportCannotBeFinishedAgain() {
        Report report = pendingReport();
        report.start();
        report.complete(anArtifact());

        assertThrows(InvalidStateException.class, () -> report.fail("too late"));
    }

    @Test
    void expiredReportIsNoLongerDownloadable() {
        Report report = pendingReport();
        report.start();
        report.complete(anArtifact());
        report.expire();

        assertNull(report.getArtifact());
        InvalidStateException error = assertThrows(InvalidStateException.class, report::requireArtifact);
        assertEquals("This report has expired and is no longer available", error.getMessage());
    }

    @Test
    void onlyCompletedReportsCanExpire() {
        Report report = pendingReport();

        assertThrows(InvalidStateException.class, report::expire);
    }

    @Test
    void intervalEndingBeforeItStartsIsRejected() {
        DomainValidationException error = assertThrows(DomainValidationException.class,
                () -> Report.request(REQUESTER, ReportType.EVENTS_BY_PERIOD, ReportFormat.CSV,
                        ReportParameters.of(END, START, null)));

        assertEquals(List.of("endAt must be after startAt"), error.getNotification().getErrors());
    }

    /** Intervalo de duração zero não apura nada; o semiaberto do sistema já o deixaria vazio. */
    @Test
    void intervalOfZeroLengthIsRejected() {
        assertThrows(DomainValidationException.class,
                () -> Report.request(REQUESTER, ReportType.EVENTS_BY_PERIOD, ReportFormat.CSV,
                        ReportParameters.of(START, START, null)));
    }

    /**
     * É a razão de ReportParameters não guardar nulo no construtor compacto, ao contrário dos VOs
     * de identidade: quem preencheu o formulário errado vê tudo que falta de uma vez.
     */
    @Test
    void everyMissingFieldIsReportedAtOnce() {
        DomainValidationException error = assertThrows(DomainValidationException.class,
                () -> Report.request(REQUESTER, null, null, ReportParameters.of(null, null, null)));

        assertEquals(
                List.of("type is required", "format is required", "startAt is required", "endAt is required"),
                error.getNotification().getErrors());
    }

    @Test
    void reportKnowsWhoOwnsIt() {
        Report report = pendingReport();

        assertTrue(report.isOwnedBy(REQUESTER));
        assertFalse(report.isOwnedBy(UserId.of(UUID.randomUUID())));
    }

    private static Report pendingReport() {
        return Report.request(REQUESTER, ReportType.EVENTS_BY_PERIOD, ReportFormat.CSV,
                ReportParameters.of(START, END, null));
    }

    private static ReportArtifact anArtifact() {
        return ReportArtifact.of("eventos-por-periodo-2026-09-04.csv", "text/csv", 128, "key.csv");
    }
}
