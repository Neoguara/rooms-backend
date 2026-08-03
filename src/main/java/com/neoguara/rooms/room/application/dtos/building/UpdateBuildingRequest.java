package com.neoguara.rooms.room.application.dtos.building;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Dados para atualização de um edifício. A atualização substitui todos os campos, \
        portanto todos são obrigatórios: campos omitidos são interpretados como nulos e resultam em 422.""")
public record UpdateBuildingRequest(
        @Schema(description = "Nome do edifício", example = "Bloco A",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Endereço do edifício", example = "Rua das Flores, 100",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String address,

        @Schema(description = "Total de andares. Deve ser no mínimo 1", example = "5", minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Integer totalFloors
) {}
