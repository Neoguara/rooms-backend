package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.enums.EventRequestStatus;
import com.neoguara.rooms.event.domain.enums.EventRequestType;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.validation.EventRequestValidation;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_requests")
public class EventRequest {

    @EmbeddedId
    private EventRequestId id;

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

    private EventRequest(
            EventId eventId,
            UserId createdBy,
            EventRequestType type,
            String justification
    ) {
        this.id = new EventRequestId();
        this.eventId = eventId;
        this.createdBy = createdBy;
        this.status = EventRequestStatus.PENDING;
        this.type = type;
        this.justification = justification;
        this.createdAt = LocalDateTime.now();
    }

    public static EventRequest create(
            EventId eventId,
            UserId createdBy,
            EventRequestType type,
            String justification
    ) {

        EventRequest request = new EventRequest(eventId, createdBy, type, justification);
        Notification notification = Notification.create();
        new EventRequestValidation().validate(request, notification);
        notification.raiseIfHasErrors();
        return request;
    }

    public void approve() {
        if (this.status != EventRequestStatus.PENDING)
            throw new InvalidStateException("Only pending requests can be approved");
        this.status = EventRequestStatus.APPROVED;
    }

    public void reject() {
        if (this.status != EventRequestStatus.PENDING)
            throw new InvalidStateException("Only pending requests can be rejected");
        this.status = EventRequestStatus.REJECTED;
    }

    public void cancel() {
        if (this.status != EventRequestStatus.PENDING)
            throw new InvalidStateException("Only pending requests can be cancelled");
        this.status = EventRequestStatus.CANCELLED;
    }

    public EventRequestId getId() {return id;}
    public EventId getEventId() {return eventId;}
    public UserId getCreatedBy() {return createdBy;}
    public EventRequestStatus getStatus() {return status;}
    public EventRequestType getType() {return type;}
    public String getJustification() {return justification;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
