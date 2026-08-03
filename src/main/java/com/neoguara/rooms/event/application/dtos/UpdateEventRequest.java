package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = """
        Dados da solicitação de atualização de evento. A solicitação descreve o estado completo \
        desejado do evento: `description`, `isAllDay`, `recurrenceRule` e `justification` são \
        opcionais e os demais campos são obrigatórios, mesmo que não tenham sido alterados.""")
public record UpdateEventRequest(
        @Schema(description = "ID do evento a ser atualizado", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID eventId,

        @Schema(description = "Novo título do evento", example = "Reunião de planejamento",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @Schema(description = "Nova descrição do evento", example = "Alinhamento trimestral da equipe",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String description,

        @Schema(description = "Novo início do evento (ISO-8601). Deve ser anterior a `endAt`",
                example = "2024-06-01T09:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime startAt,

        @Schema(description = "Novo fim do evento (ISO-8601). Deve ser posterior a `startAt`",
                example = "2024-06-01T11:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime endAt,

        @Schema(description = "Indica se o evento ocupa o dia inteiro",
                example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        Boolean isAllDay,

        @Schema(description = "Regra de recorrência do evento (formato iCalendar RRULE)",
                example = "FREQ=WEEKLY;BYDAY=MO", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String recurrenceRule,

        @Schema(description = "Justificativa da solicitação, exibida para quem aprova",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String justification,

        @Schema(description = "ID do usuário que está solicitando a atualização",
                requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,

        @Schema(description = "ID da sala reservada para o evento", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID roomId
) {}
