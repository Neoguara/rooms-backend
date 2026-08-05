package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = """
        Alteração do tipo `CREATE`: descreve um evento a ser criado. `description`, `isAllDay` e \
        `recurrenceRule` são opcionais; os demais campos são obrigatórios. O evento só passa a \
        existir quando esta alteração for aprovada.""")
public record CreateEventChange(
        @Schema(description = "Título do evento", example = "Reunião de planejamento",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @Schema(description = "Descrição do evento", example = "Alinhamento trimestral da equipe",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String description,

        @Schema(description = "Início do evento (ISO-8601). Deve ser anterior a `endAt`",
                example = "2024-06-01T09:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime startAt,

        @Schema(description = "Fim do evento (ISO-8601). Deve ser posterior a `startAt`",
                example = "2024-06-01T11:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime endAt,

        @Schema(description = "Indica se o evento ocupa o dia inteiro",
                example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        Boolean isAllDay,

        @Schema(description = "Regra de recorrência do evento (formato iCalendar RRULE)",
                example = "FREQ=WEEKLY;BYDAY=MO", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String recurrenceRule,

        @Schema(description = "ID da sala reservada para o evento", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID roomId
) implements EventChangeRequest {}
