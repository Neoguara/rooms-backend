package com.neoguara.rooms.room.application.dtos.resource;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Dados para atualização de um recurso. A atualização substitui todos os campos, \
        portanto todos são obrigatórios: campos omitidos são interpretados como nulos e resultam em 422.""")
public record UpdateResourceRequest(
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
