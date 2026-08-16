package com.neoguara.rooms.shared.domain.exceptions;

import java.util.List;

public class ConflictException extends BusinessException {

    private final transient List<String> errors;

    public ConflictException(String message) {
        super(message);
        this.errors = List.of();
    }

    /** Conflito com mais de uma causa. Todas viajam para a resposta, não só a primeira. */
    public ConflictException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = List.copyOf(errors);
    }

    /** Vazia quando o conflito tem causa única — nesse caso a mensagem já diz tudo. */
    public List<String> getErrors() {
        return errors;
    }
}
