package com.neoguara.rooms.shared.domain.exceptions;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Object identifier) {
        super(String.format("%s not found: %s", resource, identifier));
    }
}
