package com.neoguara.rooms.event.domain.services;

import java.util.Collection;
import java.util.List;

/** Um choque por sala: o que se pretende ocupar e o que já a segura no mesmo intervalo. */
public record EventConflict(OccupiedSlot attempted, OccupiedSlot occupant) {

    /** Todos os choques de {@code attempted} contra {@code occupants} — não apenas o primeiro. */
    public static List<EventConflict> against(OccupiedSlot attempted, Collection<OccupiedSlot> occupants) {
        return occupants.stream()
                .filter(attempted::competesWith)
                .map(occupant -> new EventConflict(attempted, occupant))
                .toList();
    }

    public String describe() {
        return "\"%s\" (%s to %s) collides with \"%s\" (%s to %s) in the same room"
                .formatted(
                        attempted.title(), attempted.startAt(), attempted.endAt(),
                        occupant.title(), occupant.startAt(), occupant.endAt());
    }
}
