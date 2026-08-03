package com.neoguara.rooms.room.application.dtos.resource;

import com.neoguara.rooms.room.domain.enums.ResourceStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de um recurso. Todos os campos são sempre retornados.")
public record ResourceResponse(
        @Schema(description = "ID do recurso", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "Nome do recurso", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Descrição do recurso", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,

        @Schema(description = "Identificador do ícone do recurso", requiredMode = Schema.RequiredMode.REQUIRED)
        String icon,

        @Schema(description = "Status atual do recurso", requiredMode = Schema.RequiredMode.REQUIRED)
        ResourceStatus status,

        @Schema(description = "Data de criação", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime updatedAt
) {}
