package com.neoguara.rooms.room.application.dtos.building;

public record UpdateBuildingStatusRequest(Status status) {
    public enum Status { ACTIVE, INACTIVE, ARCHIVED }
}
