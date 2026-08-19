package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = """
        Alteração do tipo `REACTIVATE`: devolve um evento cancelado ao estado ativo. Serve para \
        desfazer um cancelamento indevido, e por isso só é aceita sobre eventos cancelados. O \
        estado atual do evento é registrado automaticamente como `before` do item de alteração.
        Em um evento de série recorrente, `scope` estende a reativação às demais ocorrências. \
        Ocorrências que não estão canceladas são ignoradas no lote.""")
public record ReactivateEventChange(
        @Schema(description = "ID do evento cancelado a ser reativado", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID eventId,

        @Schema(description = "Alcance da reativação na série. Omitido, vale `THIS_OCCURRENCE`",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        ChangeScope scope
) implements EventChangeRequest {}
