package com.neoguara.rooms.shared.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Corpo padrão das respostas de erro. Todos os campos são sempre retornados.")
public record ErrorResponse(
        @Schema(description = "Código HTTP do erro", example = "422", requiredMode = Schema.RequiredMode.REQUIRED)
        int status,

        @Schema(description = "Nome do erro HTTP", example = "Unprocessable Entity",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String error,

        @Schema(description = "Mensagem do erro. Quando há múltiplas validações, traz todas separadas por '; '",
                example = "name is required; capacity must be at least 1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String message,

        @Schema(description = "Lista de mensagens de validação. Vazia quando o erro não vem de validação",
                requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> errors,

        @Schema(description = "Momento em que o erro ocorreu", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant timestamp
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, List.of(), Instant.now());
    }

    public ErrorResponse(int status, String error, List<String> errors) {
        this(status, error, String.join("; ", errors), errors, Instant.now());
    }
}
