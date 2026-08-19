package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = """
        Alteração do tipo `CANCEL`: marca um evento existente como cancelado. Só é aceita na \
        aprovação se o evento estiver ativo, e pode ser desfeita por uma alteração `REACTIVATE` \
        em um grupo novo. O estado atual do evento é registrado automaticamente como `before` do \
        item de alteração.
        Em um evento de série recorrente, `scope` estende o cancelamento às demais ocorrências. \
        Ocorrências que já não estão ativas são ignoradas no lote, para que um cancelamento avulso \
        anterior não derrube o grupo inteiro.""")
public record CancelEventChange(
        @Schema(description = "ID do evento a ser cancelado", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID eventId,

        @Schema(description = "Alcance do cancelamento na série. Omitido, vale `THIS_OCCURRENCE`",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        ChangeScope scope
) implements EventChangeRequest {}
