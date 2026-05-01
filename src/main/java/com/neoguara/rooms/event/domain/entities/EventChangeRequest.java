package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.enums.EventChangeRequestStatus;
import com.neoguara.rooms.event.domain.enums.EventChangeType;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_change_requests")
public class EventChangeRequest {

    @EmbeddedId
    private EventChangeRequestId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "event_id"))
    private EventId eventId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    private UserId createdBy;

    private String status;
    private String type;
    private String justification;

    private LocalDateTime createdAt;

    EventChangeRequest () {}

    public EventChangeRequest(
            UserId createdBy,
            String justification,
            EventChangeType type
    ) {
        this.id = new EventChangeRequestId();
        this.createdBy = createdBy;
        this.status = EventChangeRequestStatus.PENDING.name();
        this.type = type.name();
        this.justification = justification;
        this.createdAt = LocalDateTime.now();
    }

    public EventChangeRequest(
            EventId eventId,
            UserId createdBy,
            String justification,
            EventChangeType type
    ) {
        this.id = new EventChangeRequestId();
        this.eventId = eventId;
        this.createdBy = createdBy;
        this.status = EventChangeRequestStatus.PENDING.name();
        this.type = type.name();
        this.justification = justification;
        this.createdAt = LocalDateTime.now();
    }

    public EventChangeRequestId getId() { return id; }
    public EventId getEventId() { return eventId; }
    public UserId getCreatedBy() { return createdBy; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public String getJustification() { return justification; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void approve() {
        this.status = EventChangeRequestStatus.APPROVED.name();
    }

    public void reject() {
        this.status = EventChangeRequestStatus.REJECTED.name();
    }

}
