package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados da solicitação de cancelamento de evento. Apenas `justification` é opcional.")
public record CancelEventRequest(
        @Schema(description = "ID do evento a ser cancelado", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID eventId,

        @Schema(description = "ID do usuário que está solicitando o cancelamento",
                requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,

        @Schema(description = "Justificativa da solicitação, exibida para quem aprova",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String justification
) {}
