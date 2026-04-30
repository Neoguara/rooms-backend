package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.enums.EventChangeRequestStatus;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_change_requests")
public class EventChangeRequest {

    EventChangeRequest() {}

    public static EventChangeRequest create(EventChangeRequestId id, EventId eventId,
                                            UserId createdBy, String justification) {
        EventChangeRequest req = new EventChangeRequest();
        req.id = id;
        req.eventId = eventId;
        req.createdBy = createdBy;
        req.status = EventChangeRequestStatus.PENDING.name();
        req.justification = justification;
        req.createdAt = LocalDateTime.now();
        return req;
    }

    @EmbeddedId
    private EventChangeRequestId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "event_id"))
    private EventId eventId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    private UserId createdBy;

    private String status;
    private String justification;
    private LocalDateTime createdAt;

    public EventChangeRequestId getId() { return id; }
    public EventId getEventId() { return eventId; }
    public UserId getCreatedBy() { return createdBy; }
    public String getStatus() { return status; }
    public String getJustification() { return justification; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void approve() {
        this.status = EventChangeRequestStatus.APPROVED.name();
    }
}
