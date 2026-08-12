package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = """
        Uma decisão registrada sobre um grupo de alterações. Registros nunca são alterados nem \
        removidos, e como um grupo só pode ser decidido enquanto está `PENDING`, cada grupo acumula \
        no máximo um registro. Apenas `comment` pode vir nulo.""")
public record ApprovalResponse(
        @Schema(description = "ID do registro de decisão", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "ID do usuário que tomou a decisão", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID decidedBy,

        @Schema(description = "Decisão tomada: APPROVED ou REJECTED", example = "APPROVED",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String decision,

        @Schema(description = "Comentário informado por quem decidiu. Nulo quando não informado",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String comment,

        @Schema(description = "Momento em que a decisão foi tomada", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime decidedAt
) {}
