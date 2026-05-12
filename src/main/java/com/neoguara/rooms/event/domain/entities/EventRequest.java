package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.enums.EventRequestStatus;
import com.neoguara.rooms.event.domain.enums.EventRequestType;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_requests")
public class EventRequest {

    @EmbeddedId
    private EventChangeRequestId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "event_id"))
    private EventId eventId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    private UserId createdBy;

    private EventRequestStatus status;
    private EventRequestType type;
    private String justification;

    private LocalDateTime createdAt;

    EventRequest() {}

    
}
