package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = """
        Estado do evento antes (`old*`) e depois (`new*`) da alteração solicitada. Somente `id` é \
        sempre retornado: em solicitações CREATE os campos `old*` são nulos, em solicitações CANCEL \
        os campos `new*` são nulos, e campos opcionais do evento (descrição, dia inteiro e recorrência) \
        podem ser nulos em qualquer tipo.""")
public record EventChangeItemResponse(
        @Schema(description = "ID do item de alteração", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

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
