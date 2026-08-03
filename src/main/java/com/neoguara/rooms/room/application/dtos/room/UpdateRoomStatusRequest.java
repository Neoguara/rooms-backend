package com.neoguara.rooms.room.application.dtos.room;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Novo status da sala")
public record UpdateRoomStatusRequest(
        @Schema(description = "Status desejado: AVAILABLE ativa uma sala INACTIVE ou MAINTENANCE (ou restaura uma ARCHIVED), "
                + "INACTIVE desativa a sala, MAINTENANCE coloca a sala em manutenção, ARCHIVED arquiva a sala",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Status status
) {
    public enum Status { AVAILABLE, INACTIVE, ARCHIVED, MAINTENANCE }
}
