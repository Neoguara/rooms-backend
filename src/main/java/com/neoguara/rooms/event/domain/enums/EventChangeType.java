package com.neoguara.rooms.event.domain.enums;

public enum EventChangeType {
    CREATE,
    UPDATE,
    CANCEL,
    REACTIVATE,
    /** Descarta um evento criado por engano. Não é solicitável direto: só surge ao reverter um CREATE. */
    DISCARD
}
