package com.neoguara.rooms.event.application.mappers;

import com.neoguara.rooms.event.application.dtos.EventChangeItemResponse;
import com.neoguara.rooms.event.application.dtos.EventChangeRequestResponse;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;

public class EventChangeRequestMapper {

    private EventChangeRequestMapper() {}

    public static EventChangeRequestResponse toResponse(EventRequest changeRequest, EventChangeItem changeItem) {
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
        var before = item.getBefore();
        var after = item.getAfter();
        return new EventChangeItemResponse(
                item.getId().id(),
                before != null ? before.getRoomId().id() : null,
                after != null ? after.getRoomId().id() : null,
                before != null ? before.getTitle() : null,
                after != null ? after.getTitle() : null,
                before != null ? before.getDescription() : null,
                after != null ? after.getDescription() : null,
                before != null ? before.getStartAt() : null,
                after != null ? after.getStartAt() : null,
                before != null ? before.getEndAt() : null,
                after != null ? after.getEndAt() : null,
                before != null ? before.isAllDay() : null,
                after != null ? after.isAllDay() : null,
                before != null ? before.getRecurrenceRule() : null,
                after != null ? after.getRecurrenceRule() : null
        );
    }
}
