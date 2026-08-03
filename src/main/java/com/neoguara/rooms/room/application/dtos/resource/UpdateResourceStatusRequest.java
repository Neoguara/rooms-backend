package com.neoguara.rooms.room.application.dtos.resource;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Novo status do recurso")
public record UpdateResourceStatusRequest(
        @Schema(description = "Status desejado: ACTIVE ativa um recurso INACTIVE ou restaura um ARCHIVED, "
                + "INACTIVE desativa um recurso ACTIVE, ARCHIVED arquiva o recurso",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Status status
) {
    public enum Status { ACTIVE, INACTIVE, ARCHIVED }
}
