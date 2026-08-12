package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.Approval;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;

import java.util.List;

public interface ApprovalRepositoryPort {
    Approval save(Approval approval);

    /** Decisões tomadas sobre o grupo, da mais antiga para a mais recente. */
    List<Approval> findByEventRequestId(EventRequestId eventRequestId);
}
