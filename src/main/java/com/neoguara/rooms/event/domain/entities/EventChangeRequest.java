package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "event_change_requests")
public class EventChangeRequest {

    @EmbeddedId
    private EventChangeRequestId id;


    private String status;

}
