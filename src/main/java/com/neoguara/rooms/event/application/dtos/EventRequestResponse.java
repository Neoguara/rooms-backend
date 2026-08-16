package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = """
        Grupo de alterações solicitadas, aprovado ou rejeitado por inteiro. Apenas `justification` \
        e `reversalOf` podem vir nulos.""")
public record EventRequestResponse(
        @Schema(description = "ID do grupo", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "ID do usuário que submeteu o grupo", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID createdBy,

        @Schema(description = """
                Status do grupo: `PENDING` (ainda não decidido), `APPROVED` (todas as alterações \
                foram efetivadas) ou `REJECTED` (nenhuma foi). Como a decisão vale para o grupo \
                inteiro, não existe estado parcial""",
                example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
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
                Choques de sala antecipados no momento da submissão, como aviso. Vem sempre vazia \
                nas demais leituras do grupo, que não recalculam a agenda""",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<EventConflictResponse> conflicts
) {}
