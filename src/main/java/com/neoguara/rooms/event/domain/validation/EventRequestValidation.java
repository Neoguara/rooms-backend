package com.neoguara.rooms.event.domain.validation;

import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.shared.domain.validation.Validator;

public class EventRequestValidation implements Validator<EventRequest> {
    @Override
    public void validate(EventRequest target, Notification notification) {
        notification
                .addErrorIf(target.getCreatedBy() == null, "createdBy is required")
                .addErrorIf(target.getType() == null, "type is required");
    }
}
