package com.neoguara.rooms.event.application.mappers;

import com.neoguara.rooms.event.application.dtos.EventChangeItemResponse;
import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;

public class EventRequestMapper {

    private EventRequestMapper() {}

    public static EventRequestResponse toResponse(EventRequest eventRequest, EventChangeItem changeItem) {
        return new EventRequestResponse(
                eventRequest.getId().id(),
                eventRequest.getEventId() != null ? eventRequest.getEventId().id() : null,
                eventRequest.getCreatedBy().id(),
                eventRequest.getStatus().name(),
                eventRequest.getType().name(),
                eventRequest.getJustification(),
                eventRequest.getCreatedAt(),
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
