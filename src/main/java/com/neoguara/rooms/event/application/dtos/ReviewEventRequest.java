package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = """
        Decisões tomadas sobre os itens de um grupo. Não é preciso decidir todos os itens de uma \
        vez: os que não aparecerem em `decisions` continuam pendentes. Cada decisão gera um \
        registro no histórico de auditoria e é definitiva — um item já decidido não pode ser \
        decidido de novo, nem pelo mesmo revisor nem por outro. As decisões são aplicadas na \
        ordem informada, e basta uma falhar para que nenhuma seja gravada.
        Quem está decidindo é identificado pelo token de autenticação, não pelo corpo da \
        requisição.""")
public record ReviewEventRequest(
        @Schema(description = "Decisões por item. Deve conter ao menos um item",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<EventChangeItemDecision> decisions
) {}
