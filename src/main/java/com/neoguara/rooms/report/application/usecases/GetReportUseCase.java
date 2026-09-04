package com.neoguara.rooms.report.application.usecases;

import com.neoguara.rooms.report.application.dtos.ReportResponse;
import com.neoguara.rooms.report.application.mappers.ReportMapper;
import com.neoguara.rooms.report.application.ports.ReportRepositoryPort;
import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.valueobjects.ReportId;
import com.neoguara.rooms.report.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Consulta de acompanhamento. Responde igual em qualquer estado — é o cliente que decide quando
 * parar de perguntar, olhando {@code status}.
 */
@Service
public class GetReportUseCase {

    private final ReportRepositoryPort repository;

    GetReportUseCase(ReportRepositoryPort repository) {
        this.repository = repository;
    }

    /**
     * Todos os relatórios, do mais recente para o mais antigo.
     *
     * <p>Não filtra por dono, ao contrário de {@link #execute}. A diferença é deliberada: aqui só
     * saem metadados — o que foi pedido, quando e em que estado —, enquanto o arquivo em si
     * continua acessível apenas a quem o pediu. Ver a regra de posse no download.
     */
    public List<ReportResponse> findAll() {
        return repository.findAll().stream().map(ReportMapper::toResponse).toList();
    }

    public ReportResponse execute(UUID reportId, UUID requesterId) {
        Report report = repository.findById(ReportId.of(reportId))
                .orElseThrow(() -> new ResourceNotFoundException("Report", reportId));

        // Relatório de outro usuário é tratado como inexistente, e não como proibido: um 403
        // confirmaria que aquele id existe, o que já é informação a mais.
        if (!report.isOwnedBy(UserId.of(requesterId)))
            throw new ResourceNotFoundException("Report", reportId);

        return ReportMapper.toResponse(report);
    }
}
