package com.neoguara.rooms.event.domain.enums;

import java.util.Collection;

/**
 * Status de um grupo de solicitações. Não é persistido: é sempre derivado do status
 * dos itens de alteração, de modo que grupo e itens nunca possam divergir.
 */
public enum EventRequestStatus {
    PENDING,
    IN_REVIEW,
    APPROVED,
    REJECTED,
    PARTIALLY_APPROVED;

    public static EventRequestStatus from(Collection<EventChangeItemStatus> itemStatuses) {
        if (itemStatuses.isEmpty()) return PENDING;

        boolean hasPending = itemStatuses.contains(EventChangeItemStatus.PENDING);
        boolean hasApproved = itemStatuses.contains(EventChangeItemStatus.APPROVED);
        boolean hasRejected = itemStatuses.contains(EventChangeItemStatus.REJECTED);

        if (hasPending) return hasApproved || hasRejected ? IN_REVIEW : PENDING;
        if (hasApproved && hasRejected) return PARTIALLY_APPROVED;
        return hasApproved ? APPROVED : REJECTED;
    }
}
