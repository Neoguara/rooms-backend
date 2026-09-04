package com.neoguara.rooms.report.application.usecases;

import com.neoguara.rooms.report.application.ports.ReportGenerator;
import com.neoguara.rooms.report.application.ports.ReportRepositoryPort;
import com.neoguara.rooms.report.application.ports.ReportStoragePort;
import com.neoguara.rooms.report.application.services.ReportGeneratorRegistry;
import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.enums.ReportFormat;
import com.neoguara.rooms.report.domain.enums.ReportStatus;
import com.neoguara.rooms.report.domain.enums.ReportType;
import com.neoguara.rooms.report.domain.valueobjects.ReportId;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;
import com.neoguara.rooms.report.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testa o worker com {@link ReportStateWriter} de verdade sobre um repositório em memória. As
 * transações não existem fora do Spring, mas a sequência de estados é a mesma — e é ela que
 * garante que nenhuma falha deixe o relatório parado em {@code PROCESSING}.
 */
class GenerateReportUseCaseTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 1, 8, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 9, 30, 18, 0);

    @Test
    void successfulGenerationStoresTheFileAndCompletesTheReport() {
        InMemoryReports reports = new InMemoryReports();
        RecordingStorage storage = new RecordingStorage();
        Report report = reports.persist(pendingReport());

        useCase(reports, storage, parameters -> "conteúdo".getBytes(StandardCharsets.UTF_8))
                .execute(report.getId().id());

        Report stored = reports.get(report.getId());
        assertEquals(ReportStatus.COMPLETED, stored.getStatus());
        assertEquals("text/csv; charset=UTF-8", stored.getArtifact().contentType());
        assertEquals(9L, stored.getArtifact().sizeBytes());
        assertNull(stored.getFailureReason());
    }

    @Test
    void filenameCarriesTheReportTypeAndFormat() {
        InMemoryReports reports = new InMemoryReports();
        RecordingStorage storage = new RecordingStorage();
        Report report = reports.persist(pendingReport());

        useCase(reports, storage, parameters -> new byte[0]).execute(report.getId().id());

        String filename = reports.get(report.getId()).getArtifact().filename();
        assertTrue(filename.startsWith("eventos-por-periodo-"), filename);
        assertTrue(filename.endsWith(".csv"), filename);
    }

    /**
     * Ninguém está esperando a resposta: se a exceção subisse, o relatório ficaria PROCESSING para
     * sempre e o cliente faria polling sem fim.
     */
    @Test
    void failedGenerationIsRecordedOnTheReportInsteadOfEscaping() {
        InMemoryReports reports = new InMemoryReports();
        Report report = reports.persist(pendingReport());

        useCase(reports, new RecordingStorage(), parameters -> {
            throw new IllegalStateException("Room labels could not be resolved");
        }).execute(report.getId().id());

        Report stored = reports.get(report.getId());
        assertEquals(ReportStatus.FAILED, stored.getStatus());
        assertEquals("Room labels could not be resolved", stored.getFailureReason());
        assertNull(stored.getArtifact());
    }

    @Test
    void exceptionWithoutMessageStillRecordsAReason() {
        InMemoryReports reports = new InMemoryReports();
        Report report = reports.persist(pendingReport());

        useCase(reports, new RecordingStorage(), parameters -> {
            throw new NullPointerException();
        }).execute(report.getId().id());

        assertEquals("NullPointerException", reports.get(report.getId()).getFailureReason());
    }

    /**
     * Marcar PROCESSING acontece fora do try de propósito. Se um segundo worker pegasse o mesmo
     * relatório, marcá-lo como falho apagaria o resultado de quem o está gerando de verdade.
     */
    @Test
    void reportAlreadyTakenByAnotherWorkerIsNotOverwritten() {
        InMemoryReports reports = new InMemoryReports();
        Report report = reports.persist(pendingReport());
        report.start();

        GenerateReportUseCase useCase = useCase(reports, new RecordingStorage(), parameters -> new byte[0]);

        assertThrows(InvalidStateException.class, () -> useCase.execute(report.getId().id()));
        assertEquals(ReportStatus.PROCESSING, reports.get(report.getId()).getStatus());
    }

    @Test
    void reportWithoutAGeneratorForItsFormatFails() {
        InMemoryReports reports = new InMemoryReports();
        Report report = reports.persist(pendingReport());

        GenerateReportUseCase useCase = new GenerateReportUseCase(
                new ReportStateWriter(reports),
                new ReportGeneratorRegistry(List.of()),
                new RecordingStorage());

        useCase.execute(report.getId().id());

        assertEquals(ReportStatus.FAILED, reports.get(report.getId()).getStatus());
    }

    private static GenerateReportUseCase useCase(
            InMemoryReports reports,
            ReportStoragePort storage,
            CsvGeneration generation
    ) {
        ReportGenerator generator = new ReportGenerator() {
            @Override
            public boolean supports(ReportType type, ReportFormat format) {
                return type == ReportType.EVENTS_BY_PERIOD && format == ReportFormat.CSV;
            }

            @Override
            public byte[] generate(ReportParameters parameters) {
                return generation.run(parameters);
            }
        };

        return new GenerateReportUseCase(
                new ReportStateWriter(reports),
                new ReportGeneratorRegistry(List.of(generator)),
                storage);
    }

    private static Report pendingReport() {
        return Report.request(
                UserId.of(UUID.randomUUID()),
                ReportType.EVENTS_BY_PERIOD,
                ReportFormat.CSV,
                ReportParameters.of(START, END, null));
    }

    @FunctionalInterface
    private interface CsvGeneration {
        byte[] run(ReportParameters parameters);
    }

    private static final class InMemoryReports implements ReportRepositoryPort {

        private final Map<ReportId, Report> stored = new HashMap<>();

        Report persist(Report report) {
            stored.put(report.getId(), report);
            return report;
        }

        Report get(ReportId id) {
            return stored.get(id);
        }

        @Override
        public Report save(Report report) {
            stored.put(report.getId(), report);
            return report;
        }

        @Override
        public Optional<Report> findById(ReportId id) {
            return Optional.ofNullable(stored.get(id));
        }

        @Override
        public List<Report> findByStatus(ReportStatus status) {
            return stored.values().stream().filter(report -> report.getStatus() == status).toList();
        }

        @Override
        public List<Report> findAll() {
            return List.copyOf(stored.values());
        }
    }

    private static final class RecordingStorage implements ReportStoragePort {

        @Override
        public String store(ReportId id, String filename, byte[] content) {
            return id.id() + ".csv";
        }

        @Override
        public InputStream load(String storageKey) {
            return InputStream.nullInputStream();
        }

        @Override
        public void delete(String storageKey) {
        }
    }
}
