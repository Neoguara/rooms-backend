package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.enums.EventStatus;
import com.neoguara.rooms.event.domain.exceptions.EventConflictException;
import com.neoguara.rooms.event.domain.services.EventConflict;
import com.neoguara.rooms.event.domain.services.RoomOccupancy;
import com.neoguara.rooms.event.domain.validation.EventValidation;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {
    @EmbeddedId
    private EventId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "room_id"))
    private RoomId roomId;
    private String title;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Boolean isAllDay;
    private String recurrenceRule;
    private EventStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    Event() {}

    private Event(
            RoomId roomId,
            String title,
            String description,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Boolean isAllDay,
            String recurrenceRule
    ) {
        this.id = new EventId();
        this.roomId = roomId;
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isAllDay = isAllDay;
        this.recurrenceRule = recurrenceRule;
        this.status = EventStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Event create(
            RoomId roomId,
            String title,
            String description,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Boolean isAllDay,
            String recurrenceRule,
            RoomOccupancy occupancy
    ) {
        Event event = new Event(
                roomId,
                title,
                description,
                startAt,
                endAt,
                isAllDay,
                recurrenceRule
        );
        Notification notification = Notification.create();
        new EventValidation().validate(event, notification);
        notification.raiseIfHasErrors();
        event.requireFreeSlot(occupancy);
        return event;
    }

    public void update(
            RoomId roomId,
            String title,
            String description,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Boolean isAllDay,
            String recurrenceRule,
            RoomOccupancy occupancy
    ) {
        if (this.status != EventStatus.ACTIVE)
            throw new InvalidStateException("Only active events can be updated");
        this.roomId = roomId;
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isAllDay = isAllDay;
        this.recurrenceRule = recurrenceRule;
        this.updatedAt = LocalDateTime.now();
        Notification notification = Notification.create();
        new EventValidation().validate(this, notification);
        notification.raiseIfHasErrors();
        requireFreeSlot(occupancy);
    }

    public void cancel() {
        if (this.status != EventStatus.ACTIVE)
            throw new InvalidStateException("Only active events can be cancelled");
        this.status = EventStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marca o evento como descartado, para quando sua criação foi aprovada por engano. Difere de
     * {@link #cancel()}: um cancelamento é uma decisão legítima sobre um evento que existiu, e é
     * reversível; um descarte diz que o evento nunca deveria ter existido, e é definitivo.
     */
    public void discard() {
        if (this.status != EventStatus.ACTIVE && this.status != EventStatus.CANCELLED)
            throw new InvalidStateException("Only active or cancelled events can be discarded");
        this.status = EventStatus.DISCARDED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Exige a agenda porque reativar volta a ocupar a sala, e o horário pode ter sido tomado
     * enquanto o evento estava cancelado.
     */
    public void reactivate(RoomOccupancy occupancy) {
        if (this.status != EventStatus.CANCELLED)
            throw new InvalidStateException("Only cancelled events can be reactivated");
        this.status = EventStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
        requireFreeSlot(occupancy);
    }

    public void complete() {
        if (this.status != EventStatus.ACTIVE)
            throw new InvalidStateException("Only active events can be completed");
        this.status = EventStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void archive() {
        if (this.status == EventStatus.ACTIVE || this.status == EventStatus.ARCHIVED)
            throw new InvalidStateException("Only cancelled or completed events can be archived");
        this.status = EventStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Recusa o horário se alguém já segura a sala nele. A consulta apenas estreita a busca; quem
     * decide o que é choque é este método, para que a regra continue valendo qualquer que seja a
     * implementação de {@link RoomOccupancy} que chegue.
     */
    private void requireFreeSlot(RoomOccupancy occupancy) {
        List<EventConflict> conflicts = occupancy.occupying(roomId, startAt, endAt).stream()
                .filter(other -> !other.getId().equals(this.id))
                .filter(other -> other.getStatus().occupiesRoom())
                .filter(other -> other.getRoomId().equals(this.roomId))
                .filter(this::overlaps)
                .map(EventConflict::of)
                .toList();
        if (!conflicts.isEmpty()) throw new EventConflictException(conflicts);
    }

    /**
     * Intervalo semiaberto: eventos colados, em que um termina exatamente quando o outro começa,
     * não disputam a sala.
     */
    private boolean overlaps(Event other) {
        return this.startAt.isBefore(other.endAt) && this.endAt.isAfter(other.startAt);
    }

    public EventId getId() {return id;}
    public RoomId getRoomId() {return roomId;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public LocalDateTime getStartAt() {return startAt;}
    public LocalDateTime getEndAt() {return endAt;}
    public Boolean isAllDay() {return isAllDay;}
    public String getRecurrenceRule() {return recurrenceRule;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public Boolean getAllDay() {return isAllDay;}
    public EventStatus getStatus() {return status;}
}
