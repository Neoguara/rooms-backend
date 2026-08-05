package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Uma alteração do grupo junto do histórico completo de decisões tomadas sobre ela.")
public record EventChangeItemAuditResponse(
        @Schema(description = "A alteração solicitada e seu status atual",
                requiredMode = Schema.RequiredMode.REQUIRED)
        EventChangeItemResponse change,

        @Schema(description = "Decisões tomadas sobre esta alteração, da mais antiga para a mais recente. "
                + "Lista vazia enquanto ninguém tiver decidido",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<ApprovalResponse> history
) {}
