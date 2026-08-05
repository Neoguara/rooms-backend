package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = """
        Grupo de alterações com a trilha de auditoria de cada item. Apenas `justification` pode \
        vir nula.""")
public record EventRequestAuditResponse(
        @Schema(description = "ID do grupo", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "ID do usuário que submeteu o grupo", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID createdBy,

        @Schema(description = """
                Status do grupo, derivado dos itens: `PENDING`, `IN_REVIEW`, `APPROVED`, `REJECTED` \
                ou `PARTIALLY_APPROVED`""",
                example = "PARTIALLY_APPROVED", requiredMode = Schema.RequiredMode.REQUIRED)
        String status,

        @Schema(description = "Justificativa informada no grupo. Nula quando não informada",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String justification,

        @Schema(description = "Data de criação do grupo", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt,

        @Schema(description = "Alterações do grupo, cada uma com seu histórico de decisões",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<EventChangeItemAuditResponse> changes
) {}
