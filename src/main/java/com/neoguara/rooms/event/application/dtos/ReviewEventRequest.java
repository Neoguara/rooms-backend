package com.neoguara.rooms.event.application.dtos;

import com.neoguara.rooms.event.domain.enums.ApprovalDecision;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Decisão tomada sobre um grupo de alterações. A decisão vale para o grupo inteiro: aprovar \
        efetiva todas as alterações dele, na ordem em que foram submetidas, e rejeitar não efetiva \
        nenhuma. Não é possível aprovar parte de um grupo — quem quiser separar as alterações deve \
        submetê-las em grupos diferentes.
        A decisão é definitiva: um grupo já decidido não pode ser decidido de novo, nem pelo mesmo \
        revisor nem por outro. Quem está decidindo é identificado pelo token de autenticação, não \
        pelo corpo da requisição.""")
public record ReviewEventRequest(
        @Schema(description = "Decisão tomada sobre o grupo", example = "APPROVED",
                requiredMode = Schema.RequiredMode.REQUIRED)
        ApprovalDecision decision,

        @Schema(description = "Comentário de quem decidiu, preservado no histórico de auditoria",
                example = "Sala já ocupada nesse horário",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String comment
) {}
