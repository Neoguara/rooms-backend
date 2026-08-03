package com.neoguara.rooms.room.application.dtos.building;

import com.neoguara.rooms.room.domain.enums.BuildingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de um edifício. Todos os campos são sempre retornados.")
public record BuildingResponse(
        @Schema(description = "ID do edifício", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "Nome do edifício", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Endereço do edifício", requiredMode = Schema.RequiredMode.REQUIRED)
        String address,

        @Schema(description = "Total de andares", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer totalFloors,

        @Schema(description = "Status atual do edifício", requiredMode = Schema.RequiredMode.REQUIRED)
        BuildingStatus status,

        @Schema(description = "Data de criação", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime updatedAt
) {}
