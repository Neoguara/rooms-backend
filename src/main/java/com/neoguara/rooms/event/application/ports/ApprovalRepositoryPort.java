package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.Approval;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;

import java.util.Collection;
import java.util.List;

public interface ApprovalRepositoryPort {
    Approval save(Approval approval);
    List<Approval> findByEventChangeItemIdIn(Collection<EventChangeItemId> eventChangeItemIds);
}
