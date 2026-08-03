package com.neoguara.rooms.room.application.dtos.resource;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para cadastro de um recurso. Todos os campos são obrigatórios.")
public record CreateResourceRequest(
        @Schema(description = "Nome do recurso", example = "Projetor",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Descrição do recurso", example = "Projetor full HD com entrada HDMI",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String description,

        @Schema(description = "Identificador do ícone do recurso", example = "projector",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String icon
) {}
