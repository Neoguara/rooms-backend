package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = """
        Alteração do tipo `CANCEL`: marca um evento existente como cancelado. O estado atual do \
        evento é registrado automaticamente como `before` do item de alteração.""")
public record CancelEventChange(
        @Schema(description = "ID do evento a ser cancelado", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID eventId
) implements EventChangeRequest {}
