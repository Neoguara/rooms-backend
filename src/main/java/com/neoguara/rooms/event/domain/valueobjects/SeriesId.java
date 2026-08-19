package com.neoguara.rooms.event.domain.valueobjects;

import com.neoguara.rooms.shared.domain.exceptions.DomainValidationException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.Embeddable;

import java.util.UUID;

/**
 * Liga entre si as ocorrências geradas por uma mesma regra de recorrência. Não existe entidade por
 * trás: a série é o conjunto de eventos que compartilham este id, e nada além disso — cada
 * ocorrência já carrega a própria sala, o próprio horário e a própria regra.
 */
@Embeddable
public record SeriesId(UUID id) {
    public SeriesId {
        if (id == null) throw new DomainValidationException(Notification.create().addError("SeriesId must not be null"));
    }

    public SeriesId() {
        this(UUID.randomUUID());
    }

    public static SeriesId of(UUID id) {
        return new SeriesId(id);
    }
}
