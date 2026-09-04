package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = """
        Alteração do tipo `UPDATE`: descreve o estado completo desejado de um evento existente. \
        `description`, `isAllDay` e `recurrenceRule` são opcionais; os demais campos são \
        obrigatórios, mesmo os que não foram alterados. Só é aceita na aprovação se o evento \
        estiver ativo — para corrigir um evento cancelado, use antes uma alteração `REACTIVATE`.
        `recurrenceRule` não é editável: informe o valor atual do evento. Trocar o padrão de \
        repetição é encerrar a série e abrir outra, o que se expressa como `CANCEL` das ocorrências \
        restantes mais `CREATE` da nova série, no mesmo grupo.
        Em um evento de série recorrente, `scope` estende a alteração às demais ocorrências. Nos \
        alcances em lote, `startAt` e `endAt` valem como **deslocamento**: a diferença entre o \
        `startAt` informado e o do evento em `eventId` é aplicada à data de cada ocorrência, que \
        recebe o horário e a duração novos. É assim que uma série de terça inteira passa para \
        quarta.""")
public record UpdateEventChange(
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

        @Schema(description = "ID da sala reservada para o evento", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID roomId,

        @Schema(description = "Alcance da alteração na série. Omitido, vale `THIS_OCCURRENCE`",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        ChangeScope scope
) implements EventChangeRequest {}
