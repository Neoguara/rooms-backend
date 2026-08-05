package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.validation.EventRequestValidation;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Agrupa as alterações submetidas de uma só vez por um usuário. O que será feito em cada
 * evento vive nos {@link EventChangeItem} do grupo, e o status do grupo é derivado deles.
 */
@Entity
@Table(name = "event_requests")
public class EventRequest {

    @EmbeddedId
    private EventRequestId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    private UserId createdBy;

    private String justification;

    private LocalDateTime createdAt;

    EventRequest() {}

    private EventRequest(UserId createdBy, String justification) {
        this.id = new EventRequestId();
        this.createdBy = createdBy;
        this.justification = justification;
        this.createdAt = LocalDateTime.now();
    }

    public static EventRequest open(UserId createdBy, String justification) {
        EventRequest request = new EventRequest(createdBy, justification);
        Notification notification = Notification.create();
        new EventRequestValidation().validate(request, notification);
        notification.raiseIfHasErrors();
        return request;
    }

    public EventRequestId getId() {return id;}
    public UserId getCreatedBy() {return createdBy;}
    public String getJustification() {return justification;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
