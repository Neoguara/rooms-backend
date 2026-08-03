package com.neoguara.rooms.room.application.dtos.building;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Novo status do edifício")
public record UpdateBuildingStatusRequest(
        @Schema(description = "Status desejado: ACTIVE ativa um edifício INACTIVE ou restaura um ARCHIVED, "
                + "INACTIVE desativa um edifício ACTIVE, ARCHIVED arquiva o edifício",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Status status
) {
    public enum Status { ACTIVE, INACTIVE, ARCHIVED }
}
