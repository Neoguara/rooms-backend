package com.neoguara.rooms.room.application.dtos.room;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Lista de recursos que substituirá os recursos atuais da sala")
public record ReplaceRoomResourcesRequest(
        @Schema(description = "IDs dos recursos da sala. Obrigatório; envie uma lista vazia para remover todos os recursos",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<UUID> resourceIds
) {}
