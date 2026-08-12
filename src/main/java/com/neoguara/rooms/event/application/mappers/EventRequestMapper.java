package com.neoguara.rooms.event.application.mappers;

import com.neoguara.rooms.event.application.dtos.ApprovalResponse;
import com.neoguara.rooms.event.application.dtos.EventChangeItemResponse;
import com.neoguara.rooms.event.application.dtos.EventRequestAuditResponse;
import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.domain.entities.Approval;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;

import java.util.List;

public class EventRequestMapper {

    private EventRequestMapper() {}

    public static EventRequestResponse toResponse(EventRequest eventRequest, List<EventChangeItem> changeItems) {
        return new EventRequestResponse(
                eventRequest.getId().id(),
                eventRequest.getCreatedBy().id(),
                eventRequest.getStatus().name(),
                eventRequest.getReversalOf() != null ? eventRequest.getReversalOf().id() : null,
                eventRequest.getJustification(),
                eventRequest.getCreatedAt(),
                changeItems.stream().map(EventRequestMapper::toItemResponse).toList()
        );
    }

    public static EventRequestAuditResponse toAuditResponse(
            EventRequest eventRequest,
            List<EventChangeItem> changeItems,
            List<Approval> approvals
    ) {
        return new EventRequestAuditResponse(
                eventRequest.getId().id(),
                eventRequest.getCreatedBy().id(),
                eventRequest.getStatus().name(),
                eventRequest.getReversalOf() != null ? eventRequest.getReversalOf().id() : null,
                eventRequest.getJustification(),
                eventRequest.getCreatedAt(),
                changeItems.stream().map(EventRequestMapper::toItemResponse).toList(),
                approvals.stream().map(EventRequestMapper::toApprovalResponse).toList()
        );
    }

    private static EventChangeItemResponse toItemResponse(EventChangeItem item) {
        var before = item.getBefore();
        var after = item.getAfter();
        return new EventChangeItemResponse(
                item.getId().id(),
                item.getType().name(),
                item.getEventId() != null ? item.getEventId().id() : null,
                item.getReversalOf() != null ? item.getReversalOf().id() : null,
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

    private static ApprovalResponse toApprovalResponse(Approval approval) {
        return new ApprovalResponse(
                approval.getId().id(),
                approval.getDecidedBy().id(),
                approval.getDecision().name(),
                approval.getComment(),
                approval.getDecidedAt()
        );
    }
}
