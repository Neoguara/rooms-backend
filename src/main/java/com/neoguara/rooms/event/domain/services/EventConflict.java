package com.neoguara.rooms.event.domain.services;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.valueobjects.EventId;

import java.time.LocalDateTime;

/** Um evento que já segura a sala no horário pretendido. */
public record EventConflict(
        EventId eventId,
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
    public static EventConflict of(Event event) {
        return new EventConflict(event.getId(), event.getTitle(), event.getStartAt(), event.getEndAt());
    }

    public String describe() {
        return "Room is already taken from %s to %s by event \"%s\" (%s)"
                .formatted(startAt, endAt, title, eventId.id());
    }
}
