package com.neoguara.rooms.event.domain.validation;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.shared.domain.validation.Validator;

public class EventValidation implements Validator<Event> {
    @Override
    public void validate(Event target, Notification notification) {
        notification
                .addErrorIf(target.getRoomId() == null, "roomId is required")
                .addErrorIf(target.getTitle() == null || target.getTitle().isBlank(), "title is required")
                .addErrorIf(target.getStartAt() == null, "startAt is required")
                .addErrorIf(target.getEndAt() == null, "endAt is required")
                .addErrorIf( target.getStartAt() != null && target.getEndAt() != null && !target.getStartAt().isBefore(target.getEndAt()), "startAt must be before endAt");
    }
}
