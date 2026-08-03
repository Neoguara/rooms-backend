package com.neoguara.rooms.room.application.dtos.room;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Recursos atualmente associados à sala. Todos os campos são sempre retornados.")
public record RoomResourcesResponse(
        @Schema(description = "ID da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID roomId,

        @Schema(description = "IDs dos recursos associados à sala", requiredMode = Schema.RequiredMode.REQUIRED)
        List<UUID> resourceIds
) {}
