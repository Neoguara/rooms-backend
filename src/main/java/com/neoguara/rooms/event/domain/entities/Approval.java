package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.enums.ApprovalDecision;
import com.neoguara.rooms.event.domain.validation.ApprovalValidation;
import com.neoguara.rooms.event.domain.valueobjects.ApprovalId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Registro imutável da decisão tomada sobre um grupo de alterações. Nunca é atualizado: cada
 * aprovação ou rejeição insere uma nova linha, formando o histórico de auditoria.
 */
@Entity
@Table(name = "approvals")
public class Approval {
    @EmbeddedId
    private ApprovalId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "event_request_id"))
    private EventRequestId eventRequestId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    private UserId decidedBy;

    @Enumerated(EnumType.STRING)
    private ApprovalDecision decision;

    private String comment;

    private LocalDateTime decidedAt;

    Approval() {}

    private Approval(
            EventRequestId eventRequestId,
            UserId decidedBy,
            ApprovalDecision decision,
            String comment
    ) {
        this.id = new ApprovalId();
        this.eventRequestId = eventRequestId;
        this.decidedBy = decidedBy;
        this.decision = decision;
        this.comment = comment;
        this.decidedAt = LocalDateTime.now();
    }

    public static Approval of(
            EventRequestId eventRequestId,
            UserId decidedBy,
            ApprovalDecision decision,
            String comment
    ) {
        Approval approval = new Approval(eventRequestId, decidedBy, decision, comment);
        Notification notification = Notification.create();
        new ApprovalValidation().validate(approval, notification);
        notification.raiseIfHasErrors();
        return approval;
    }

    public ApprovalId getId() { return id; }
    public EventRequestId getEventRequestId() { return eventRequestId; }
    public UserId getDecidedBy() { return decidedBy; }
    public ApprovalDecision getDecision() { return decision; }
    public String getComment() { return comment; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
}
