package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = """
        Dados de um evento. `description`, `isAllDay` e `recurrenceRule` podem vir nulos \
        quando não informados na solicitação; os demais campos são sempre retornados.""")
public record EventResponse(
        @Schema(description = "ID do evento", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "ID da sala reservada para o evento", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID roomId,

        @Schema(description = "Título do evento", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @Schema(description = "Descrição do evento", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String description,

        @Schema(description = "Início do evento", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime startAt,

        @Schema(description = "Fim do evento", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime endAt,

        @Schema(description = "Indica se o evento ocupa o dia inteiro",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        Boolean isAllDay,

        @Schema(description = "Regra de recorrência do evento (formato iCalendar RRULE)",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String recurrenceRule,

        @Schema(description = """
                ID da série que agrupa as ocorrências geradas por uma mesma regra de recorrência. \
                Nulo em evento avulso. Não existe recurso próprio de série: para listar as irmãs, \
                filtre os eventos por este id""",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        UUID seriesId,

        @Schema(description = "Status do evento: ACTIVE, CANCELLED, COMPLETED ou ARCHIVED",
                example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
        String status,

        @Schema(description = "Data de criação", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime updatedAt
) {}
