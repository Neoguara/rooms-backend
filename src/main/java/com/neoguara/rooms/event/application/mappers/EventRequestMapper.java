package com.neoguara.rooms.event.application.mappers;

import com.neoguara.rooms.event.application.dtos.ApprovalResponse;
import com.neoguara.rooms.event.application.dtos.EventChangeItemAuditResponse;
import com.neoguara.rooms.event.application.dtos.EventChangeItemResponse;
import com.neoguara.rooms.event.application.dtos.EventRequestAuditResponse;
import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.domain.entities.Approval;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.enums.EventRequestStatus;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;

import java.util.List;
import java.util.Map;

public class EventRequestMapper {

    private EventRequestMapper() {}

    public static EventRequestResponse toResponse(EventRequest eventRequest, List<EventChangeItem> changeItems) {
        return new EventRequestResponse(
                eventRequest.getId().id(),
                eventRequest.getCreatedBy().id(),
                statusOf(changeItems).name(),
                eventRequest.getJustification(),
                eventRequest.getCreatedAt(),
                changeItems.stream().map(EventRequestMapper::toItemResponse).toList()
        );
    }

    public static EventRequestAuditResponse toAuditResponse(
            EventRequest eventRequest,
            List<EventChangeItem> changeItems,
            Map<EventChangeItemId, List<Approval>> approvalsByItem
    ) {
        return new EventRequestAuditResponse(
                eventRequest.getId().id(),
                eventRequest.getCreatedBy().id(),
                statusOf(changeItems).name(),
                eventRequest.getJustification(),
                eventRequest.getCreatedAt(),
                changeItems.stream()
                        .map(item -> new EventChangeItemAuditResponse(
                                toItemResponse(item),
                                approvalsByItem.getOrDefault(item.getId(), List.of()).stream()
                                        .map(EventRequestMapper::toApprovalResponse)
                                        .toList()
                        ))
                        .toList()
        );
    }

    private static EventRequestStatus statusOf(List<EventChangeItem> changeItems) {
        return EventRequestStatus.from(changeItems.stream().map(EventChangeItem::getStatus).toList());
    }

    private static EventChangeItemResponse toItemResponse(EventChangeItem item) {
        var before = item.getBefore();
        var after = item.getAfter();
        return new EventChangeItemResponse(
                item.getId().id(),
                item.getType().name(),
                item.getEventId() != null ? item.getEventId().id() : null,
                item.getStatus().name(),
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
