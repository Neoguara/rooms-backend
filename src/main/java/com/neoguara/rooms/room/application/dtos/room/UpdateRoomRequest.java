package com.neoguara.rooms.room.application.dtos.room;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = """
        Dados para atualização de uma sala. A atualização substitui todos os campos, portanto todos \
        são obrigatórios: campos omitidos são interpretados como nulos (ou zero, no caso de `floor` e \
        `capacity`) e resultam em 422. Os recursos da sala são alterados em PUT /rooms/{id}/resources.""")
public record UpdateRoomRequest(
        @Schema(description = "Nome da sala", example = "Sala de Reuniões 1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Código identificador da sala", example = "A-101",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String code,

        @Schema(description = "ID do tipo de sala", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID roomTypeId,

        @Schema(description = "ID do edifício onde a sala fica", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID buildingId,

        @Schema(description = "Andar da sala. Deve ser zero ou maior", example = "1", minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED)
        int floor,

        @Schema(description = "Capacidade da sala. Deve ser no mínimo 1", example = "10", minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        int capacity
) {}
