package com.neoguara.rooms.room.application.dtos.room;

import com.neoguara.rooms.room.domain.enums.RoomStatus;

public record UpdateRoomStatusRequest(RoomStatus status) {}
