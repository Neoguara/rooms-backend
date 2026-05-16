package com.neoguara.rooms.event.domain.validation;

import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventSnapshot;
import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.shared.domain.validation.Validator;

public class EventChangeItemValidation implements Validator<EventChangeItem> {
    @Override
    public void validate(EventChangeItem target, Notification notification) {
        notification
                .addErrorIf(target.getEventRequestId() == null, "eventRequestId is required")
                .addErrorIf(target.getBefore() == null && target.getAfter() == null, "before and after cannot both be null");

        if (target.getBefore() != null) validateSnapshot(target.getBefore(), notification);
        if (target.getAfter() != null) validateSnapshot(target.getAfter(), notification);
    }

    private void validateSnapshot(EventSnapshot snapshot, Notification notification) {
        notification
                .addErrorIf(snapshot.getRoomId() == null, "roomId is required")
                .addErrorIf(snapshot.getTitle() == null || snapshot.getTitle().isBlank(), "title is required")
                .addErrorIf(snapshot.getStartAt() == null, "startAt is required")
                .addErrorIf(snapshot.getEndAt() == null, "endAt is required")
                .addErrorIf(
                        snapshot.getStartAt() != null && snapshot.getEndAt() != null && !snapshot.getStartAt().isBefore(snapshot.getEndAt()),
                        "startAt must be before endAt"
                );
    }
}
