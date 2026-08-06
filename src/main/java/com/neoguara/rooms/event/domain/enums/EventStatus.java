package com.neoguara.rooms.event.domain.enums;

public enum EventStatus {
    ACTIVE,
    CANCELLED,
    COMPLETED,
    ARCHIVED,
    /** Evento que nunca deveria ter existido: sua criação foi aprovada por engano e depois revertida. */
    DISCARDED,
}
