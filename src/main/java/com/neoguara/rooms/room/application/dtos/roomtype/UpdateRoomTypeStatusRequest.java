package com.neoguara.rooms.room.application.dtos.roomtype;

import com.neoguara.rooms.room.domain.enums.RoomTypeStatus;

public record UpdateRoomTypeStatusRequest(RoomTypeStatus status) {}
