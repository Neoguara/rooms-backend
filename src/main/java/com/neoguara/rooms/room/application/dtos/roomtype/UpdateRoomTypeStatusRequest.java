package com.neoguara.rooms.room.application.dtos.roomtype;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Novo status do tipo de sala")
public record UpdateRoomTypeStatusRequest(
        @Schema(description = "Status desejado: ACTIVE ativa um tipo INACTIVE ou restaura um ARCHIVED, "
                + "INACTIVE desativa um tipo ACTIVE, ARCHIVED arquiva o tipo de sala",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Status status
) {
    public enum Status { ACTIVE, INACTIVE, ARCHIVED }
}
