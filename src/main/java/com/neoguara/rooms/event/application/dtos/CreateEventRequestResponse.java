package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Solicitação registrada. Apenas `justification` pode vir nulo.")
public record CreateEventRequestResponse (
        @Schema(description = "ID da solicitação", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "ID do usuário que criou a solicitação", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID createdBy,

        @Schema(description = "Status da solicitação: PENDING, APPROVED ou REJECTED",
                example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
        String status,

        @Schema(description = "Tipo da solicitação: CREATE, UPDATE ou CANCEL",
                example = "CREATE", requiredMode = Schema.RequiredMode.REQUIRED)
        String type,

        @Schema(description = "Justificativa informada na solicitação. Nulo quando não informada",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String justification,

        @Schema(description = "Data de criação da solicitação", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt
) {}
