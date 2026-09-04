package com.neoguara.rooms.report.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.Embeddable;

import java.util.UUID;

/**
 * Referência ao usuário que pediu o relatório. É um tipo próprio do módulo, e não o {@code UserId}
 * de {@code user} ou de {@code event}, para que {@code report} não passe a depender deles só por
 * causa de uma chave estrangeira.
 */
@Embeddable
public record UserId(UUID id) {
    public UserId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("UserId must not be null"));
    }

    public static UserId of(UUID id) {
        return new UserId(id);
    }
}
