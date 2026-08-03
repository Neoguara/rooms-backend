package com.neoguara.rooms.user.application.dtos;

import com.neoguara.rooms.user.domain.enums.UserRole;
import com.neoguara.rooms.user.domain.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de um usuário. Todos os campos são sempre retornados.")
public record UserResponse(
        @Schema(description = "ID do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "Nome do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Email do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @Schema(description = "Papel do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
        UserRole role,

        @Schema(description = "Status atual do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
        UserStatus status,

        @Schema(description = "Data de criação", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime updatedAt
) {}
