package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = """
        Uma alteração solicitada, com o estado do evento antes (`old*`) e depois (`new*`) dela. \
        Não tem status próprio: quem é aprovado ou rejeitado é o grupo que a contém. \
        Em alterações CREATE os campos `old*` são nulos e `eventId` só é preenchido após a \
        aprovação, quando o evento passa a existir; em alterações CANCEL, REACTIVATE e DISCARD os \
        campos `new*` são nulos; e campos opcionais do evento (descrição, dia inteiro e \
        recorrência) podem ser nulos em qualquer tipo.""")
public record EventChangeItemResponse(
        @Schema(description = "ID do item de alteração", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "Tipo da alteração: CREATE, UPDATE, CANCEL, REACTIVATE ou DISCARD. "
                + "DISCARD não é solicitável direto: só é gerado ao reverter um CREATE aprovado",
                example = "CREATE", requiredMode = Schema.RequiredMode.REQUIRED)
        String type,

        @Schema(description = "ID do evento alterado. Em alterações do tipo CREATE é nulo enquanto "
                + "a alteração está pendente ou foi rejeitada, e passa a apontar para o evento "
                + "criado assim que ela é aprovada",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        UUID eventId,

        @Schema(description = "ID do item que esta alteração desfaz. "
                + "Nulo em alterações que não são reversões",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        UUID reversalOf,

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
