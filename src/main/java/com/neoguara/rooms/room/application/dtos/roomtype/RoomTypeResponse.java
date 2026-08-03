package com.neoguara.rooms.room.application.dtos.roomtype;

import com.neoguara.rooms.room.domain.enums.RoomTypeStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de um tipo de sala. Todos os campos são sempre retornados.")
public record RoomTypeResponse(
        @Schema(description = "ID do tipo de sala", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "Nome do tipo de sala", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Descrição do tipo de sala", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,

        @Schema(description = "Capacidade padrão do tipo de sala", requiredMode = Schema.RequiredMode.REQUIRED)
        String defaultCapacity,

        @Schema(description = "Cor associada ao tipo de sala", requiredMode = Schema.RequiredMode.REQUIRED)
        String color,

        @Schema(description = "Identificador do ícone do tipo de sala", requiredMode = Schema.RequiredMode.REQUIRED)
        String icon,

        @Schema(description = "Status atual do tipo de sala", requiredMode = Schema.RequiredMode.REQUIRED)
        RoomTypeStatus status,

        @Schema(description = "Data de criação", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime updatedAt
) {}
