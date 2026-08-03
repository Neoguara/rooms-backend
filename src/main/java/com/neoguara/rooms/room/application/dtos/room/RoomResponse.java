package com.neoguara.rooms.room.application.dtos.room;

import com.neoguara.rooms.room.domain.enums.RoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de uma sala. Todos os campos são sempre retornados.")
public record RoomResponse(
        @Schema(description = "ID da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "Nome da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Código identificador da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        String code,

        @Schema(description = "ID do tipo de sala", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID roomTypeId,

        @Schema(description = "ID do edifício onde a sala fica", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID buildingId,

        @Schema(description = "Andar da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        int floor,

        @Schema(description = "Capacidade da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        int capacity,

        @Schema(description = "Status atual da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        RoomStatus status,

        @Schema(description = "Data de criação", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime updatedAt
) {}
