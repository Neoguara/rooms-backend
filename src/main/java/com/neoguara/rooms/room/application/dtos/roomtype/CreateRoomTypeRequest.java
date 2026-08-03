package com.neoguara.rooms.room.application.dtos.roomtype;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para cadastro de um tipo de sala. Todos os campos são obrigatórios.")
public record CreateRoomTypeRequest(
        @Schema(description = "Nome do tipo de sala", example = "Auditório",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Descrição do tipo de sala", example = "Sala ampla para apresentações",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String description,

        @Schema(description = "Capacidade padrão do tipo de sala", example = "50",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String defaultCapacity,

        @Schema(description = "Cor associada ao tipo de sala", example = "#3B82F6",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String color,

        @Schema(description = "Identificador do ícone do tipo de sala", example = "auditorium",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String icon
) {}
