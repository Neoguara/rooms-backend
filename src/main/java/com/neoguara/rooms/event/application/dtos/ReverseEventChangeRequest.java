package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Pedido de reversão de uma decisão. A alteração que desfaz o item é derivada pelo sistema \
        a partir dos snapshots guardados, então nada além da justificativa precisa ser informado. \
        Quem está pedindo é identificado pelo token de autenticação.""")
public record ReverseEventChangeRequest(
        @Schema(description = "Motivo da reversão, exibido para quem aprova",
                example = "Aprovei o item errado na revisão",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String justification
) {}
