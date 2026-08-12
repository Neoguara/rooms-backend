package com.neoguara.rooms.event.domain.validation;

import com.neoguara.rooms.event.domain.entities.Approval;
import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.shared.domain.validation.Validator;

public class ApprovalValidation implements Validator<Approval> {
    @Override
    public void validate(Approval target, Notification notification) {
        notification
                .addErrorIf(target.getEventRequestId() == null, "eventRequestId is required")
                .addErrorIf(target.getDecidedBy() == null, "decidedBy is required")
                .addErrorIf(target.getDecision() == null, "decision is required");
    }
}
