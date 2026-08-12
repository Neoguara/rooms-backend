package com.neoguara.rooms.event.domain.enums;

/**
 * Status de um grupo de solicitações. A decisão é tomada sobre o grupo inteiro, então não existe
 * estado intermediário: ou nada foi decidido, ou tudo foi aprovado, ou tudo foi rejeitado.
 */
public enum EventRequestStatus {
    PENDING,
    APPROVED,
    REJECTED
}
