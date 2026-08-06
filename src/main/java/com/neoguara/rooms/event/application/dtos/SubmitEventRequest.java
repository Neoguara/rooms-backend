package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = """
        Grupo de alterações submetidas de uma só vez. Criações, atualizações, cancelamentos e \
        reativações podem ser misturados na mesma lista `changes`, que deve conter ao menos um \
        item e preserva a ordem informada. Apenas `justification` é opcional.
        Quem está submetendo é identificado pelo token de autenticação, não pelo corpo da \
        requisição.""")
public record SubmitEventRequest(
        @Schema(description = "Justificativa do grupo, exibida para quem aprova",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String justification,

        @Schema(description = "Alterações solicitadas, aplicadas na ordem informada. "
                + "Deve conter ao menos um item",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<EventChangeRequest> changes
) {}
