package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = """
        Uma alteração solicitada, com o estado do evento antes (`old*`) e depois (`new*`) dela. \
        Em alterações CREATE os campos `old*` e `eventId` são nulos, em alterações CANCEL e \
        REACTIVATE os campos `new*` são nulos, e campos opcionais do evento (descrição, dia \
        inteiro e recorrência) podem ser nulos em qualquer tipo.""")
public record EventChangeItemResponse(
        @Schema(description = "ID do item de alteração", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "Tipo da alteração: CREATE, UPDATE, CANCEL ou REACTIVATE",
                example = "CREATE", requiredMode = Schema.RequiredMode.REQUIRED)
        String type,

        @Schema(description = "ID do evento alterado. Nulo em alterações do tipo CREATE, "
                + "pois o evento só passa a existir após a aprovação",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        UUID eventId,

        @Schema(description = "Status desta alteração: PENDING, APPROVED ou REJECTED",
                example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
        String status,

        @Schema(description = "Sala antes da alteração", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        UUID oldRoomId,

        @Schema(description = "Sala depois da alteração", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        UUID newRoomId,

        @Schema(description = "Título antes da alteração", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String oldTitle,

        @Schema(description = "Título depois da alteração", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String newTitle,

        @Schema(description = "Descrição antes da alteração", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String oldDescription,

        @Schema(description = "Descrição depois da alteração", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String newDescription,

        @Schema(description = "Início antes da alteração", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        LocalDateTime oldStartAt,

        @Schema(description = "Início depois da alteração", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        LocalDateTime newStartAt,

        @Schema(description = "Fim antes da alteração", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        LocalDateTime oldEndAt,

        @Schema(description = "Fim depois da alteração", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        LocalDateTime newEndAt,

        @Schema(description = "Indicador de dia inteiro antes da alteração",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        Boolean oldIsAllDay,

        @Schema(description = "Indicador de dia inteiro depois da alteração",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        Boolean newIsAllDay,

        @Schema(description = "Regra de recorrência antes da alteração",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String oldRecurrenceRule,

        @Schema(description = "Regra de recorrência depois da alteração",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String newRecurrenceRule
) {}
