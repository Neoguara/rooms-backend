package com.neoguara.rooms.room.application.dtos.resource;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Novo status do recurso")
public record UpdateResourceStatusRequest(
        @Schema(description = "Status desejado: ACTIVE ativa um recurso INACTIVE, "
                + "INACTIVE desativa um recurso ACTIVE (obrigatório antes de deletar)",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Status status
) {
    public enum Status { ACTIVE, INACTIVE }
}
