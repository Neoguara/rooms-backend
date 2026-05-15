package com.neoguara.rooms.room.application.dtos.roomtype;

public record UpdateRoomTypeStatusRequest(Status status) {
    public enum Status { ACTIVE, INACTIVE }
}
