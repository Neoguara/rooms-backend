package com.neoguara.rooms.room.application.dtos.room;

public record UpdateRoomStatusRequest(Status status) {
    public enum Status { AVAILABLE, INACTIVE, ARCHIVED, MAINTENANCE }
}
