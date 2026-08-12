package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = """
        Grupo de alterações com a trilha de auditoria da decisão tomada sobre ele. Apenas \
        `justification` e `reversalOf` podem vir nulos.""")
public record EventRequestAuditResponse(
        @Schema(description = "ID do grupo", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "ID do usuário que submeteu o grupo", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID createdBy,

        @Schema(description = "Status do grupo: `PENDING`, `APPROVED` ou `REJECTED`",
                example = "APPROVED", requiredMode = Schema.RequiredMode.REQUIRED)
        String status,

        @Schema(description = "ID do grupo cuja decisão este grupo desfaz. "
                + "Nulo em grupos que não são reversões",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        UUID reversalOf,

        @Schema(description = "Justificativa informada no grupo. Nula quando não informada",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String justification,

        @Schema(description = "Data de criação do grupo", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt,

        @Schema(description = "Alterações que compõem o grupo, na ordem em que serão aplicadas",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<EventChangeItemResponse> changes,

        @Schema(description = """
                Decisões tomadas sobre o grupo, da mais antiga para a mais recente. Hoje a lista \
                tem no máximo um item — vazia enquanto ninguém tiver decidido, com um único \
                registro depois — porque um grupo decidido não pode ser decidido de novo. É uma \
                lista para acomodar, sem quebrar o contrato, um fluxo futuro em que decisões \
                possam ser revistas.""",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<ApprovalResponse> history
) {}
