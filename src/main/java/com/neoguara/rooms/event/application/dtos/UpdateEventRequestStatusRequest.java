package com.neoguara.rooms.event.application.dtos;

import com.neoguara.rooms.event.domain.enums.EventRequestStatus;

public record UpdateEventRequestStatusRequest(EventRequestStatus status) {}
