package com.neoguara.rooms.event.application.mappers;

import com.neoguara.rooms.event.application.dtos.EventResponse;
import com.neoguara.rooms.event.domain.entities.Event;

public class EventMapper {

    private EventMapper() {}

    public static EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId().id(),
                event.getRoomId().id(),
                event.getTitle(),
                event.getDescription(),
                event.getStartAt(),
                event.getEndAt(),
                event.isAllDay(),
                event.getRecurrenceRule(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
