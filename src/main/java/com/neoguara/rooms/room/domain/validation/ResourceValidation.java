package com.neoguara.rooms.room.domain.validation;

import com.neoguara.rooms.room.domain.entities.Resource;
import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.shared.domain.validation.Validator;

public class ResourceValidation implements Validator<Resource> {
    @Override
    public void validate(Resource target, Notification notification) {
        notification
                .addErrorIf(target.getName() == null || target.getName().isBlank(), "name is required")
                .addErrorIf(target.getDescription() == null || target.getDescription().isBlank(), "description is required")
                .addErrorIf(target.getIcon() == null || target.getIcon().isBlank(), "icon is required");
    }
}
