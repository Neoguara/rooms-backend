package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = """
        Decisões tomadas sobre os itens de um grupo. Não é preciso decidir todos os itens de uma \
        vez: os que não aparecerem em `decisions` continuam pendentes. Cada decisão gera um \
        registro no histórico de auditoria.""")
public record ReviewEventRequest(
        @Schema(description = "ID do usuário que está decidindo", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID reviewedBy,

        @Schema(description = "Decisões por item. Deve conter ao menos um item",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<EventChangeItemDecision> decisions
) {}
