package com.neoguara.rooms.room.domain.validation;

import com.neoguara.rooms.room.domain.entities.Building;
import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.shared.domain.validation.Validator;

public class BuildingValidator implements Validator<Building> {

    @Override
    public void validate(Building target, Notification notification) {
        notification
                .addErrorIf(target.getName() == null || target.getName().isBlank(), "name is required")
                .addErrorIf(target.getAddress() == null || target.getAddress().isBlank(), "address is required")
                .addErrorIf(target.getTotalFloors() == null || target.getTotalFloors() < 1, "totalFloors must be at least 1");
    }
}
