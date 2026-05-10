package com.neoguara.rooms.shared.domain.validation;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Notification {

    private final List<String> errors = new ArrayList<>();

    private Notification() {}

    public static Notification create() {
        return new Notification();
    }

    public Notification addError(String message) {
        this.errors.add(message);
        return this;
    }

    public Notification addErrorIf(boolean condition, String message) {
        if (condition) this.errors.add(message);
        return this;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public Notification merge(Notification other) {
        this.errors.addAll(other.errors);
        return this;
    }

    public void raiseIfHasErrors() {
        if (hasErrors()) {
            throw new DomainValidationException(this);
        }
    }
}
