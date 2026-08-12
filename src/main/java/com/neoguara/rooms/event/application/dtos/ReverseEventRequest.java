package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Pedido de reversão da decisão tomada sobre um grupo. As alterações que desfazem o grupo são \
        derivadas pelo sistema a partir dos snapshots guardados, então nada além da justificativa \
        precisa ser informado. Quem está pedindo é identificado pelo token de autenticação.""")
public record ReverseEventRequest(
        @Schema(description = "Motivo da reversão, exibido para quem aprova",
                example = "Aprovei o grupo errado na revisão",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String justification
) {}
