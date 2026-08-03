package com.neoguara.rooms.user.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Novo status do usuário")
public record UpdateUserStatusRequest(
        @Schema(description = "Status desejado: ACTIVE ativa um usuário INACTIVE, INACTIVE desativa um usuário ACTIVE",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Status status
) {
    public enum Status { ACTIVE, INACTIVE }
}
