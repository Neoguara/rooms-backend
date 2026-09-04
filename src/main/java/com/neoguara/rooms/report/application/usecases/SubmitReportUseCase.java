package com.neoguara.rooms.report.application.usecases;

import com.neoguara.rooms.report.application.dtos.ReportResponse;
import com.neoguara.rooms.report.application.dtos.SubmitReportRequest;
import com.neoguara.rooms.report.application.events.ReportRequested;
import com.neoguara.rooms.report.application.mappers.ReportMapper;
import com.neoguara.rooms.report.application.ports.ReportRepositoryPort;
import com.neoguara.rooms.report.application.services.ReportGeneratorRegistry;
import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;
import com.neoguara.rooms.report.domain.valueobjects.UserId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Registra o pedido e devolve na hora. A geração acontece depois, disparada pelo evento publicado
 * aqui — quem chama recebe um id para acompanhar, não o arquivo.
 */
@Service
public class SubmitReportUseCase {

    private final ReportRepositoryPort repository;
    private final ReportGeneratorRegistry generators;
    private final ApplicationEventPublisher publisher;

    SubmitReportUseCase(
            ReportRepositoryPort repository,
            ReportGeneratorRegistry generators,
            ApplicationEventPublisher publisher
    ) {
        this.repository = repository;
        this.generators = generators;
        this.publisher = publisher;
    }

    @Transactional
        public ReportResponse execute(UUID requestedBy, SubmitReportRequest request) {
        Report report = Report.request(
                UserId.of(requestedBy),
                request.type(),
                request.format(),
                ReportParameters.of(request.startAt(), request.endAt(), request.roomId())
        );

        // Confere que alguém sabe gerar esta combinação antes de aceitar o pedido. Aceitar primeiro
        // e descobrir no worker trocaria um 404 imediato por um relatório FAILED alguns segundos
        // depois, que é muito mais difícil de entender do outro lado.
        generators.require(report.getType(), report.getFormat());

        Report saved = repository.save(report);

        // Publicado dentro da transação, mas entregue só depois do commit: o worker relê do banco,
        // e um relatório ainda não gravado seria lido como inexistente.
        publisher.publishEvent(new ReportRequested(saved.getId().id()));

        return ReportMapper.toResponse(saved);
    }
}
