package com.neoguara.rooms.event.application.mappers;

import com.neoguara.rooms.event.application.dtos.EventChangeItemResponse;
import com.neoguara.rooms.event.application.dtos.EventChangeRequestResponse;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventChangeRequest;

public class EventChangeRequestMapper {

    private EventChangeRequestMapper() {}

    public static EventChangeRequestResponse toResponse(EventChangeRequest changeRequest, EventChangeItem changeItem) {
        return new EventChangeRequestResponse(
                changeRequest.getId().id(),
                changeRequest.getEventId() != null ? changeRequest.getEventId().id() : null,
                changeRequest.getCreatedBy().id(),
                changeRequest.getStatus(),
                changeRequest.getType(),
                changeRequest.getJustification(),
                changeRequest.getCreatedAt(),
                changeItem != null ? toItemResponse(changeItem) : null
        );
    }

    private static EventChangeItemResponse toItemResponse(EventChangeItem item) {
        return new EventChangeItemResponse(
                item.getId().id(),
                item.getOldRoomId() != null ? item.getOldRoomId().id() : null,
                item.getNewRoomId() != null ? item.getNewRoomId().id() : null,
                item.getOldTitle(),
                item.getNewTitle(),
                item.getOldDescription(),
                item.getNewDescription(),
                item.getOldStartAt(),
                item.getNewStartAt(),
                item.getOldEndAt(),
                item.getNewEndAt(),
                item.getOldIsAllDay(),
                item.getNewIsAllDay(),
                item.getOldRecurrenceRule(),
                item.getNewRecurrenceRule()
        );
    }
}
