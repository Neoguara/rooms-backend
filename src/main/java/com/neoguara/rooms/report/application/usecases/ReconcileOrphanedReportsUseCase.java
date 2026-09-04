package com.neoguara.rooms.report.application.usecases;

import com.neoguara.rooms.report.application.ports.ReportRepositoryPort;
import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.enums.ReportStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Encerra os relatórios que ficaram em {@code PROCESSING} quando a aplicação caiu no meio da
 * geração. Sem isto eles ficariam nesse estado para sempre, com o cliente fazendo polling sem fim.
 *
 * <p>Não confundir com o que {@code republish-outstanding-events-on-restart} resolve. Aquilo cobre
 * o relatório que ficou em {@code PENDING} porque o worker nunca chegou a acordar, e o reprocessa.
 * Já quem parou em {@code PROCESSING} não pode ser reprocessado: {@code start()} recusa, e a
 * publicação republicada falharia em todo boot daqui em diante. Um é retomável, o outro não.
 *
 * <p>O corte é {@code startedAt} anterior a {@code bootedAt}. Relatórios que o próprio boot atual
 * colocou em {@code PROCESSING} — inclusive os que o republish acabou de destravar — têm carimbo
 * posterior e são deixados em paz, o que elimina a corrida entre as duas rotinas.
 *
 * <p>O instante da subida chega por parâmetro, e não de um {@code LocalDateTime.now()} guardado no
 * construtor. Além de deixar o caso de uso sem relógio escondido, evita o segundo construtor que
 * essa alternativa exigiria para os testes — e que impediria o Spring de instanciar o bean.
 */
@Service
public class ReconcileOrphanedReportsUseCase {

    private static final String REASON = "Interrupted by application shutdown";

    private final ReportRepositoryPort repository;

    ReconcileOrphanedReportsUseCase(ReportRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public int execute(LocalDateTime bootedAt) {
        List<Report> orphaned = repository.findByStatus(ReportStatus.PROCESSING).stream()
                .filter(report -> report.getStartedAt() != null)
                .filter(report -> report.getStartedAt().isBefore(bootedAt))
                .toList();

        orphaned.forEach(report -> {
            report.fail(REASON);
            repository.save(report);
        });

        return orphaned.size();
    }
}
