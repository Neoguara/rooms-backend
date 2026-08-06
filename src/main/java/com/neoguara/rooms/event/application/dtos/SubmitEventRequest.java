package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = """
        Grupo de alterações submetidas de uma só vez. Criações, atualizações, cancelamentos e \
        reativações podem ser misturados na mesma lista `changes`, que deve conter ao menos um \
        item e preserva a ordem informada. Apenas `justification` é opcional.""")
public record SubmitEventRequest(
        @Schema(description = "ID do usuário que está submetendo as alterações",
                requiredMode = Schema.RequiredMode.REQUIRED)
        UUID userId,

        @Schema(description = "Justificativa do grupo, exibida para quem aprova",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String justification,

        @Schema(description = "Alterações solicitadas. Deve conter ao menos um item",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<EventChangeRequest> changes
) {}
