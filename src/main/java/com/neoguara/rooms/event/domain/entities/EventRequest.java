package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.enums.EventRequestStatus;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.validation.EventRequestValidation;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Agrupa as alterações submetidas de uma só vez por um usuário. O que será feito em cada evento
 * vive nos {@link EventChangeItem} do grupo, e é o grupo — não cada item — que é aprovado ou
 * rejeitado: os itens são indivisíveis, valem todos ou nenhum.
 */
@Entity
@Table(name = "event_requests")
public class EventRequest {

    @EmbeddedId
    private EventRequestId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    private UserId createdBy;

    @Enumerated(EnumType.STRING)
    private EventRequestStatus status;

    /** Grupo cuja decisão este grupo desfaz. Nulo em grupos que não são reversões. */
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "reversal_of"))
    private EventRequestId reversalOf;

    private String justification;

    private LocalDateTime createdAt;

    EventRequest() {}

    private EventRequest(UserId createdBy, String justification) {
        this.id = new EventRequestId();
        this.createdBy = createdBy;
        this.status = EventRequestStatus.PENDING;
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

    /** Registra que este grupo existe para desfazer a decisão tomada sobre {@code original}. */
    public void markAsReversalOf(EventRequestId original) {
        this.reversalOf = original;
    }

    public void approve() {
        requirePending();
        this.status = EventRequestStatus.APPROVED;
    }

    public void reject() {
        requirePending();
        this.status = EventRequestStatus.REJECTED;
    }

    private void requirePending() {
        if (this.status != EventRequestStatus.PENDING)
            throw new InvalidStateException("Only pending event requests can be reviewed");
    }

    public EventRequestId getId() {return id;}
    public UserId getCreatedBy() {return createdBy;}
    public EventRequestStatus getStatus() {return status;}
    public EventRequestId getReversalOf() {return reversalOf;}
    public String getJustification() {return justification;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
