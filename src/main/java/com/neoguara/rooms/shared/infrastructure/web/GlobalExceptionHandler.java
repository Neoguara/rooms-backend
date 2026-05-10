package com.neoguara.rooms.shared.infrastructure.web;

import com.neoguara.rooms.shared.domain.exceptions.BusinessException;
import com.neoguara.rooms.shared.domain.exceptions.ConflictException;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", message);
    }
}
