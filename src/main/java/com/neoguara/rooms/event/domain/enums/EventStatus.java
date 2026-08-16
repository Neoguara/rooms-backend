package com.neoguara.rooms.event.domain.enums;

import java.util.EnumSet;
import java.util.Set;

public enum EventStatus {
    ACTIVE,
    CANCELLED,
    COMPLETED,
    ARCHIVED,
    /** Evento que nunca deveria ter existido: sua criação foi aprovada por engano e depois revertida. */
    DISCARDED;

    /**
     * Estados em que um evento segura a sala. Um evento concluído continua segurando: ele de fato
     * aconteceu, e aceitar outro evento por cima reescreveria o histórico da sala.
     */
    private static final Set<EventStatus> OCCUPYING = EnumSet.of(ACTIVE, COMPLETED);

    /**
     * Diz se um evento neste estado impede que outro ocupe a mesma sala no mesmo intervalo. É a
     * única fonte dessa regra: tanto a busca por salas disponíveis quanto a checagem de conflito
     * na escrita perguntam aqui, para que as duas nunca discordem.
     */
    public boolean occupiesRoom() {
        return OCCUPYING.contains(this);
    }

    public static Set<EventStatus> occupying() {
        return OCCUPYING;
    }
}
