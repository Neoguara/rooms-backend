package com.neoguara.rooms.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Token JWT e dados do usuário autenticado")
public record TokenResponse(
        @Schema(description = "Token JWT a ser enviado no header Authorization como Bearer",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String token,

        @Schema(description = "ID do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "Nome do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Email do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @Schema(description = "Papel do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
        String role,

        @Schema(description = "Indica se o usuário está ativo", requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isActive,

        @Schema(description = "Data de criação do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime updatedAt,

        @Schema(description = "Data de remoção do usuário. Nulo enquanto o usuário não for removido",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        LocalDateTime deletedAt
) {}
