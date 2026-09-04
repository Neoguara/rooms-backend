package com.neoguara.rooms.report.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = """
        Estado de um relatório. Enquanto `status` for `PENDING` ou `PROCESSING`, o arquivo ainda \
        não existe e `downloadUrl` vem nulo.""")
public record ReportResponse(
        @Schema(description = "ID do relatório", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "ID do usuário que pediu o relatório", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID requestedBy,

        @Schema(description = "O que o relatório apura", example = "EVENTS_BY_PERIOD",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String type,

        @Schema(description = "Formato do arquivo", example = "CSV",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String format,

        @Schema(description = """
                `PENDING` (na fila), `PROCESSING` (sendo gerado), `COMPLETED` (pronto para \
                download), `FAILED` (a geração falhou, veja `failureReason`) ou `EXPIRED` (o \
                arquivo foi descartado)""",
                example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
        String status,

        @Schema(description = "Início do intervalo apurado", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime startAt,

        @Schema(description = "Fim do intervalo apurado", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime endAt,

        @Schema(description = "Sala à qual o relatório foi restrito. Nulo quando apura todas",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        UUID roomId,

        @Schema(description = "Quando o relatório foi pedido", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime requestedAt,

        @Schema(description = "Quando a geração começou. Nulo enquanto `PENDING`",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        LocalDateTime startedAt,

        @Schema(description = "Quando a geração terminou, com sucesso ou não. "
                + "Nulo enquanto não terminou",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        LocalDateTime finishedAt,

        @Schema(description = "Motivo da falha. Preenchido apenas quando `status` é `FAILED`",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String failureReason,

        @Schema(description = "Nome sugerido do arquivo. Nulo enquanto não concluído",
                example = "eventos-por-periodo-2026-09-04.csv",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String filename,

        @Schema(description = "Tamanho do arquivo em bytes. Nulo enquanto não concluído",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        Long sizeBytes,

        @Schema(description = "Endereço para baixar o arquivo. Preenchido apenas quando `status` "
                + "é `COMPLETED`",
                example = "/reports/123e4567-e89b-12d3-a456-426614174000/download",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String downloadUrl
) {}
