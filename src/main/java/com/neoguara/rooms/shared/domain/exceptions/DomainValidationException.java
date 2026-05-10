package com.neoguara.rooms.shared.domain.exceptions;

import com.neoguara.rooms.shared.domain.validation.Notification;

public class DomainValidationException extends BusinessException {

    private final Notification notification;

    public DomainValidationException(Notification notification) {
        super(String.join("; ", notification.getErrors()));
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }
}
