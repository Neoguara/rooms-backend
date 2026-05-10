package com.neoguara.rooms.shared.domain.exceptions;

public abstract class BusinessException extends RuntimeException {

    protected BusinessException(String message) {
        super(message);
    }
}
