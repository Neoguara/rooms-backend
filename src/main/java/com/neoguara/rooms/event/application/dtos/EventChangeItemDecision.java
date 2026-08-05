package com.neoguara.rooms.event.application.dtos;

import com.neoguara.rooms.event.domain.enums.ApprovalDecision;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Decisão sobre um item de alteração. Apenas `comment` é opcional.")
public record EventChangeItemDecision(
        @Schema(description = "ID do item de alteração sendo decidido",
                requiredMode = Schema.RequiredMode.REQUIRED)
        UUID itemId,

        @Schema(description = "Decisão tomada sobre o item", example = "APPROVED",
                requiredMode = Schema.RequiredMode.REQUIRED)
        ApprovalDecision decision,

        @Schema(description = "Comentário de quem decidiu, preservado no histórico de auditoria",
                example = "Sala já ocupada nesse horário",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String comment
) {}
