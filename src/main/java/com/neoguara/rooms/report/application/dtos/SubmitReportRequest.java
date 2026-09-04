package com.neoguara.rooms.report.application.dtos;

import com.neoguara.rooms.report.domain.enums.ReportFormat;
import com.neoguara.rooms.report.domain.enums.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = """
        Pedido de geração de relatório. O arquivo não é produzido nesta requisição: ela apenas \
        registra o pedido e devolve um id para acompanhamento.
        Quem está pedindo é identificado pelo token de autenticação, não pelo corpo da requisição.""")
public record SubmitReportRequest(
        @Schema(description = "O que o relatório apura. Hoje só `EVENTS_BY_PERIOD`, a agenda de "
                + "eventos do intervalo",
                example = "EVENTS_BY_PERIOD", requiredMode = Schema.RequiredMode.REQUIRED)
        ReportType type,

        @Schema(description = "Formato do arquivo: `CSV` ou `PDF`",
                example = "CSV", requiredMode = Schema.RequiredMode.REQUIRED)
        ReportFormat format,

        @Schema(description = "Início do intervalo apurado", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime startAt,

        @Schema(description = "Fim do intervalo apurado, posterior a `startAt`",
                requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime endAt,

        @Schema(description = "Restringe o relatório a uma sala. Omitido, apura todas as salas",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        UUID roomId
) {}
