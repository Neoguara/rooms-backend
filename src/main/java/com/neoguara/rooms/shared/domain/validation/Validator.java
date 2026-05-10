package com.neoguara.rooms.shared.domain.validation;

public interface Validator<T> {
    void validate(T target, Notification notification);
}