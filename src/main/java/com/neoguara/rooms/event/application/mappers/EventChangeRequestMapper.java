package com.neoguara.rooms.event.application.mappers;

import com.neoguara.rooms.event.application.dtos.EventChangeRequestResponse;
import com.neoguara.rooms.event.domain.entities.EventChangeRequest;

public class EventChangeRequestMapper {

    private EventChangeRequestMapper() {}

    public static EventChangeRequestResponse toResponse(EventChangeRequest req) {
        return new EventChangeRequestResponse(
                req.getId().id(),
                req.getEventId().id(),
                req.getCreatedBy().id(),
                req.getStatus(),
                req.getJustification(),
                req.getCreatedAt()
        );
    }
}
