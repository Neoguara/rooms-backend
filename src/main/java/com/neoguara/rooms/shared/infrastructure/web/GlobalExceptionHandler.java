package com.neoguara.rooms.shared.infrastructure.web;

import com.neoguara.rooms.shared.domain.exceptions.BusinessException;
import com.neoguara.rooms.shared.domain.exceptions.ConflictException;
import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleDomainValidation(DomainValidationException ex) {
        return new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "DOMAIN_VALIDATION_ERROR",
                ex.getNotification().getErrors()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(ConflictException ex) {
        return new ErrorResponse(HttpStatus.CONFLICT.value(), "CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(InvalidStateException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleInvalidState(InvalidStateException ex) {
        return new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "INVALID_STATE", ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBusiness(BusinessException ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "BUSINESS_ERROR", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = "Invalid value '%s' for parameter '%s': expected type %s"
                .formatted(ex.getValue(), ex.getName(), expectedType);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "TYPE_MISMATCH", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Malformed JSON request";
        if (ex.getCause() instanceof JsonMappingException jme) {
            String field = jme.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(Objects::nonNull)
                    .reduce((a, b) -> a + "." + b)
                    .orElse(null);
            message = field != null
                    ? "Invalid value for field '%s': %s".formatted(field, jme.getOriginalMessage())
                    : jme.getOriginalMessage();
        }
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "DESERIALIZATION_ERROR", message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", errors);
    }
}
