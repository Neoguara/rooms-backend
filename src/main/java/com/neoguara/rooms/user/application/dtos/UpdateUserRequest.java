package com.neoguara.rooms.user.application.dtos;

import com.neoguara.rooms.user.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = """
        Dados para atualização de um usuário. `name` e `email` são obrigatórios; \
        `password` e `role` são opcionais e, quando omitidos, mantêm o valor atual.""")
public record UpdateUserRequest(
        @Schema(description = "Nome do usuário", example = "Maria Silva",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String name,

        @Schema(description = "Email do usuário", example = "maria@exemplo.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email String email,

        @Schema(description = "Nova senha. Opcional: se omitida ou em branco, a senha atual é mantida. Mínimo de 6 caracteres",
                example = "novaSenha123", minLength = 6,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        @Size(min = 6) String password,

        @Schema(description = "Novo papel do usuário. Opcional: se omitido, o papel atual é mantido",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        UserRole role
) {}
