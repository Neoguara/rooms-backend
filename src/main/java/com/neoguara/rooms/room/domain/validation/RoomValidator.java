package com.neoguara.rooms.room.domain.validation;

import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.shared.domain.validation.Validator;

public class RoomValidator implements Validator<Room> {
    @Override
    public void validate(Room target, Notification notification) {
        notification
                .addErrorIf(target.getName() == null || target.getName().isBlank(), "name is required")
                .addErrorIf(target.getCode() == null || target.getCode().isBlank(), "code is required")
                .addErrorIf(target.getType() == null || target.getType().isBlank(), "type is required")
                .addErrorIf(target.getRoomTypeId() == null, "roomTypeId is required")
                .addErrorIf(target.getBuildingId() == null, "buildingId is required")
                .addErrorIf(target.getFloor() == null || target.getFloor() < 0, "floor must be zero or greater")
                .addErrorIf(target.getCapacity() == null || target.getCapacity() < 1, "capacity must be at least 1");
    }
}
