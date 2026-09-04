package com.neoguara.rooms.report.infrastructure.listeners;

import com.neoguara.rooms.report.application.usecases.ReconcileOrphanedReportsUseCase;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Dispara a varredura de órfãos assim que a aplicação sobe.
 *
 * <p>Roda em {@code ApplicationReadyEvent}, depois do republish do Modulith — que acontece antes,
 * na subida. A ordem não é crítica porque o corte é por {@code startedAt}, mas rodar por último
 * evita marcar como interrompido um relatório que o republish acabou de retomar.
 */
@Component
public class OrphanedReportListener {

    private final ReconcileOrphanedReportsUseCase reconcileOrphanedReportsUseCase;

    /**
     * Carimbado na criação do bean, durante o refresh do contexto — antes, portanto, de qualquer
     * relatório que esta execução venha a começar, inclusive os retomados pelo republish.
     */
    private final LocalDateTime bootedAt = LocalDateTime.now();

    OrphanedReportListener(ReconcileOrphanedReportsUseCase reconcileOrphanedReportsUseCase) {
        this.reconcileOrphanedReportsUseCase = reconcileOrphanedReportsUseCase;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void failReportsInterruptedByShutdown() {
        reconcileOrphanedReportsUseCase.execute(bootedAt);
    }
}
