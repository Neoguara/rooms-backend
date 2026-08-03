package com.neoguara.rooms.room.application.dtos.room;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Dados para cadastro de uma sala. Apenas `resourceIds` é opcional.")
public record CreateRoomRequest(
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
        int capacity,

        @Schema(description = "Recursos associados à sala. Opcional: se omitido, a sala é criada sem recursos",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        List<UUID> resourceIds
) {}
