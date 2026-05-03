package com.neoguara.rooms.event.application.mappers;

import com.neoguara.rooms.event.application.dtos.EventChangeRequestResponse;
import com.neoguara.rooms.event.domain.entities.EventChangeRequest;

public class EventChangeRequestMapper {

    private EventChangeRequestMapper() {}

    public static EventChangeRequestResponse toResponse(EventChangeRequest changeRequest) {
        return new EventChangeRequestResponse(
                changeRequest.getId().id(),
                changeRequest.getEventId() != null ? changeRequest.getEventId().id() : null,
                changeRequest.getCreatedBy().id(),
                changeRequest.getStatus(),
                changeRequest.getType(),
                changeRequest.getJustification(),
                changeRequest.getCreatedAt()
        );
    }
}
