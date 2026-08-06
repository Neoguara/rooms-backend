package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Uma alteração do grupo junto do histórico completo de decisões tomadas sobre ela.")
public record EventChangeItemAuditResponse(
        @Schema(description = "A alteração solicitada e seu status atual",
                requiredMode = Schema.RequiredMode.REQUIRED)
        EventChangeItemResponse change,

        @Schema(description = """
                Decisões tomadas sobre esta alteração, da mais antiga para a mais recente. Hoje a \
                lista tem no máximo um item — vazia enquanto ninguém tiver decidido, com um único \
                registro depois — porque um item decidido não pode ser decidido de novo. É uma \
                lista para acomodar, sem quebrar o contrato, um fluxo futuro em que decisões \
                possam ser revistas.""",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<ApprovalResponse> history
) {}
