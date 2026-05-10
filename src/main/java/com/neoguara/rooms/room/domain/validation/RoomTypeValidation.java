package com.neoguara.rooms.room.domain.validation;

import com.neoguara.rooms.room.domain.entities.RoomType;
import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.shared.domain.validation.Validator;

public class RoomTypeValidation implements Validator<RoomType> {
    @Override
    public void validate(RoomType target, Notification notification) {
        notification
                .addErrorIf(target.getName() == null || target.getName().isBlank(), "name is required")
                .addErrorIf(target.getDescription() == null || target.getDescription().isBlank(), "description is required")
                .addErrorIf(target.getDefaultCapacity() == null || target.getDefaultCapacity().isBlank(), "defaultCapacity is required")
                .addErrorIf(target.getColor() == null || target.getColor().isBlank(), "color is required")
                .addErrorIf(target.getIcon() == null || target.getIcon().isBlank(), "icon is required");
    }
}
