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
                .addErrorIf(target.getPosition() == null || target.getPosition() < 0,
                        "position is required and must not be negative")
                .addErrorIf(target.getType() == null, "type is required");

        if (target.getType() != null) {
            switch (target.getType()) {
                case CREATE -> notification
                        .addErrorIf(target.getEventId() != null, "eventId must not be informed on CREATE changes")
                        .addErrorIf(target.getBefore() != null, "before must not be informed on CREATE changes")
                        .addErrorIf(target.getAfter() == null, "after is required on CREATE changes");
                case UPDATE -> notification
                        .addErrorIf(target.getEventId() == null, "eventId is required on UPDATE changes")
                        .addErrorIf(target.getBefore() == null, "before is required on UPDATE changes")
                        .addErrorIf(target.getAfter() == null, "after is required on UPDATE changes");
                case CANCEL -> notification
                        .addErrorIf(target.getEventId() == null, "eventId is required on CANCEL changes")
                        .addErrorIf(target.getBefore() == null, "before is required on CANCEL changes")
                        .addErrorIf(target.getAfter() != null, "after must not be informed on CANCEL changes");
                case REACTIVATE -> notification
                        .addErrorIf(target.getEventId() == null, "eventId is required on REACTIVATE changes")
                        .addErrorIf(target.getBefore() == null, "before is required on REACTIVATE changes")
                        .addErrorIf(target.getAfter() != null, "after must not be informed on REACTIVATE changes");
                case DISCARD -> notification
                        .addErrorIf(target.getEventId() == null, "eventId is required on DISCARD changes")
                        .addErrorIf(target.getBefore() == null, "before is required on DISCARD changes")
                        .addErrorIf(target.getAfter() != null, "after must not be informed on DISCARD changes");
            }
        }

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
