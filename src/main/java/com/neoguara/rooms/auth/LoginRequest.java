package com.neoguara.rooms.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais de acesso")
public record LoginRequest(
        @Schema(description = "Email cadastrado do usuário", example = "usuario@exemplo.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email String email,

        @Schema(description = "Senha do usuário", example = "senha123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String password
) {}
