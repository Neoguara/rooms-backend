package com.neoguara.rooms.report.domain.valueobjects;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Recorte do que o relatório apura: o intervalo e, opcionalmente, uma sala. {@code roomId} nulo
 * significa todas as salas — é a diferença entre "a agenda da sala 302" e "a agenda inteira".
 *
 * <p>É plano de propósito. Agrupar o intervalo num {@code ReportPeriod} aninhado seria mais bonito,
 * mas embeddable dentro de embeddable com {@code ddl-auto=update} é onde as colunas saem com nome
 * inesperado sem ninguém perceber.
 *
 * <p>Também é o único VO do módulo sem guarda de nulo no construtor compacto, ao contrário de
 * {@link ReportId} e {@link UserId}. Estes carregam identidade, que ou existe ou é bug; este carrega
 * entrada do usuário. Estourar no construtor devolveria o primeiro campo faltante e escondia os
 * demais — deixando a checagem para {@code ReportValidation}, o cliente recebe todos de uma vez.
 */
@Embeddable
public record ReportParameters(LocalDateTime startAt, LocalDateTime endAt, UUID roomId) {

    public static ReportParameters of(LocalDateTime startAt, LocalDateTime endAt, UUID roomId) {
        return new ReportParameters(startAt, endAt, roomId);
    }

    /** Diz se o recorte é de uma sala só, para que o gerador escolha entre as duas consultas. */
    public boolean isRoomScoped() {
        return roomId != null;
    }
}
