package com.neoguara.rooms.room.domain.validation;

import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.shared.domain.validation.Validator;

public class RoomValidator implements Validator<Room> {
    @Override
    public void validate(Room target, Notification notification) {
//        notification
//                .addErrorIf(target.getName() == null || target.getName().isBlank(), "name is required")
//                .addErrorIf(target.getCode() == null || target.getCode().isBlank(), "code is required")
//                .addErrorIf(target.getCapacity() <= 0, "capacity must be greater than 0")
//                .addErrorIf(target.getFloor() < 0, "floor must be 0 or greater");

    }
}
