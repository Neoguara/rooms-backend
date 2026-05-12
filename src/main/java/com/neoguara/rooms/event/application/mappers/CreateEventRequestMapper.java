package com.neoguara.rooms.event.application.mappers;

import com.neoguara.rooms.event.application.dtos.CreateEventRequest;
import com.neoguara.rooms.event.application.dtos.CreateEventRequestResponse;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.valueobjects.UserId;

public class CreateEventRequestMapper {

    private CreateEventRequestMapper() {}

    public static EventRequest toDomain(CreateEventRequest eventChangeRequest) {

        return EventRequest.create(
                UserId.of(eventChangeRequest.userId()),
                eventChangeRequest.justification()
        );
    }

    public static CreateEventRequestResponse toResponse(EventRequest req) {

            return new CreateEventRequestResponse(
                    req.getId().id(),
                    req.getCreatedBy().id(),
                    req.getStatus(),
                    req.getType(),
                    req.getJustification(),
                    req.getCreatedAt()
            );
    }

}
