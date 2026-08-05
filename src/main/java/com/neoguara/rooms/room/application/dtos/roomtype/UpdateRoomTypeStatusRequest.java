package com.neoguara.rooms.room.application.dtos.roomtype;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Novo status do tipo de sala")
public record UpdateRoomTypeStatusRequest(
        @Schema(description = "Status desejado: ACTIVE ativa um tipo INACTIVE, "
                + "INACTIVE desativa um tipo ACTIVE (obrigatório antes de deletar)",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Status status
) {
    public enum Status { ACTIVE, INACTIVE }
}
