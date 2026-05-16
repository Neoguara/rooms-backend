package com.neoguara.rooms.room.application.dtos.resource;

public record UpdateResourceStatusRequest(Status status) {
    public enum Status { ACTIVE, INACTIVE, ARCHIVED }
}
