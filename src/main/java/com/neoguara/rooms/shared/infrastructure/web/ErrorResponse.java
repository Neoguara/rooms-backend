package com.neoguara.rooms.shared.infrastructure.web;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        int status,
        String error,
        String message,
        List<String> errors,
        Instant timestamp
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, List.of(), Instant.now());
    }

    public ErrorResponse(int status, String error, List<String> errors) {
        this(status, error, String.join("; ", errors), errors, Instant.now());
    }
}
