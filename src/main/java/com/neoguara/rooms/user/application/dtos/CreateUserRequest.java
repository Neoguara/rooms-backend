package com.neoguara.rooms.user.application.dtos;

import com.neoguara.rooms.user.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro de um usuário. Todos os campos são obrigatórios.")
public record CreateUserRequest(
        @Schema(description = "Nome do usuário", example = "Maria Silva",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String name,

        @Schema(description = "Email do usuário. Deve ser único", example = "maria@exemplo.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email String email,

        @Schema(description = "Senha do usuário. Mínimo de 6 caracteres", example = "senha123",
                minLength = 6, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(min = 6) String password,

        @Schema(description = "Papel do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull UserRole role
) {}
