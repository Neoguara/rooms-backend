package com.neoguara.rooms.shared.infrastructure.web;

import com.neoguara.rooms.shared.domain.exceptions.BusinessException;
import com.neoguara.rooms.shared.domain.exceptions.ConflictException;
import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Responde 404 para URLs que não existem. Sem este tratamento o 404 do Spring vira um forward
     * para {@code /error}, que o filtro de autenticação não processa — a resposta que chegaria ao
     * cliente seria um 403 enganoso, sugerindo falta de permissão em vez de rota inexistente.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoResourceFound(NoResourceFoundException ex) {
        return notFound(ex.getHttpMethod().name(), ex.getResourcePath());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoHandlerFound(NoHandlerFoundException ex) {
        return notFound(ex.getHttpMethod(), ex.getRequestURL());
    }

    /** Responde 405 quando a URL existe mas não aceita o método usado, pelo mesmo motivo. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String supported = ex.getSupportedMethods() == null
                ? "none"
                : String.join(", ", ex.getSupportedMethods());
        return new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), "METHOD_NOT_ALLOWED",
                "Method %s is not supported here. Supported methods: %s".formatted(ex.getMethod(), supported));
    }

    private static ErrorResponse notFound(String method, String path) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "NOT_FOUND",
                "No endpoint %s %s".formatted(method, path.startsWith("/") ? path : "/" + path));
    }

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
        return ex.getErrors().isEmpty()
                ? new ErrorResponse(HttpStatus.CONFLICT.value(), "CONFLICT", ex.getMessage())
                : new ErrorResponse(HttpStatus.CONFLICT.value(), "CONFLICT", ex.getErrors());
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
        DatabindException cause = firstDatabindException(ex);

        if (cause instanceof UnrecognizedPropertyException unknown) {
            String known = unknown.getKnownPropertyIds().stream()
                    .map(String::valueOf)
                    .sorted()
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("none");
            return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "UNKNOWN_FIELD",
                    "Unknown field '%s'. Accepted fields here: %s".formatted(pathOf(unknown), known));
        }

        if (cause instanceof InvalidTypeIdException invalidType) {
            String accepted = acceptedTypeIds(invalidType.getBaseType().getRawClass());
            return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "UNKNOWN_TYPE",
                    "Unknown type '%s'%s%s".formatted(
                            invalidType.getTypeId(),
                            at(pathOf(invalidType)),
                            accepted == null ? "" : ". Accepted types: " + accepted));
        }

        if (cause instanceof InvalidFormatException invalidFormat) {
            return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INVALID_VALUE",
                    "Invalid value '%s'%s%s".formatted(
                            abbreviate(String.valueOf(invalidFormat.getValue())),
                            at(pathOf(invalidFormat)),
                            expectationOf(invalidFormat.getTargetType())));
        }

        if (cause instanceof MismatchedInputException mismatch) {
            return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INVALID_VALUE",
                    "Invalid value%s%s".formatted(
                            at(pathOf(mismatch)), expectationOf(mismatch.getTargetType())));
        }

        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "DESERIALIZATION_ERROR",
                "Malformed JSON request");
    }

    private String at(String path) {
        return path == null ? "" : " for field '%s'".formatted(path);
    }

    private String abbreviate(String value) {
        return value.length() <= 60 ? value : value.substring(0, 57) + "...";
    }

    /**
     * Descreve, em termos da API, o que o campo esperava receber. Devolve string vazia para tipos
     * sem uma descrição melhor do que o próprio nome da classe, que não deve vazar na resposta.
     */
    private String expectationOf(Class<?> type) {
        if (type == null) return "";
        if (type.isEnum()) {
            String values = Arrays.stream(type.getEnumConstants())
                    .map(String::valueOf)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            return ": expected one of " + values;
        }
        String expectation = switch (type.getSimpleName()) {
            case "LocalDateTime" -> "a date and time like 2026-09-01T10:00:00";
            case "LocalDate" -> "a date like 2026-09-01";
            case "LocalTime" -> "a time like 10:00:00";
            case "UUID" -> "a UUID like 123e4567-e89b-12d3-a456-426614174000";
            case "Boolean", "boolean" -> "true or false";
            case "Integer", "int", "Long", "long", "Short", "short", "BigInteger" -> "a whole number";
            case "Double", "double", "Float", "float", "BigDecimal" -> "a number";
            case "String" -> "a text value";
            default -> null;
        };
        return expectation == null ? "" : ": expected " + expectation;
    }

    /** Valores aceitos no discriminador de uma hierarquia polimórfica, lidos de {@code @JsonSubTypes}. */
    private String acceptedTypeIds(Class<?> baseType) {
        JsonSubTypes subTypes = baseType.getAnnotation(JsonSubTypes.class);
        if (subTypes == null) return null;
        return Arrays.stream(subTypes.value())
                .map(JsonSubTypes.Type::name)
                .filter(name -> !name.isBlank())
                .sorted()
                .reduce((a, b) -> a + ", " + b)
                .orElse(null);
    }

    /** Percorre a cadeia de causas até achar o erro de desserialização, que nem sempre é a causa direta. */
    private DatabindException firstDatabindException(Throwable ex) {
        for (Throwable cause = ex.getCause(); cause != null; cause = cause.getCause()) {
            if (cause instanceof DatabindException databind) return databind;
        }
        return null;
    }

    /** Caminho do campo dentro do JSON, como `changes[0].titulo`. */
    private String pathOf(DatabindException ex) {
        StringBuilder path = new StringBuilder();
        for (DatabindException.Reference reference : ex.getPath()) {
            if (reference.getPropertyName() != null) {
                if (!path.isEmpty()) path.append('.');
                path.append(reference.getPropertyName());
            } else if (reference.getIndex() >= 0) {
                path.append('[').append(reference.getIndex()).append(']');
            }
        }
        return path.isEmpty() ? null : path.toString();
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
