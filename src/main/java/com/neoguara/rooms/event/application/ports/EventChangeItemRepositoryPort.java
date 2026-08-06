package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;

import java.util.Collection;
import java.util.List;

public interface EventChangeItemRepositoryPort {
    EventChangeItem save (EventChangeItem eventChangeItem);
    List<EventChangeItem> saveAll (List<EventChangeItem> eventChangeItems);
    /** Itens do grupo, na ordem em que foram submetidos. */
    List<EventChangeItem> findByEventRequestId(EventRequestId eventRequestId);

    /** Indica se já existe uma alteração criada para desfazer a decisão deste item. */
    boolean hasReversal(EventChangeItemId eventChangeItemId);
    List<EventChangeItem> findByEventRequestIdIn(Collection<EventRequestId> eventRequestIds);
}
