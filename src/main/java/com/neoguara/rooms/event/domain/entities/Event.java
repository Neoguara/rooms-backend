package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.enums.EventStatus;
import com.neoguara.rooms.event.domain.exceptions.EventConflictException;
import com.neoguara.rooms.event.domain.services.EventConflict;
import com.neoguara.rooms.event.domain.services.OccupiedSlot;
import com.neoguara.rooms.event.domain.services.RoomOccupancy;
import com.neoguara.rooms.event.domain.validation.EventValidation;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import com.neoguara.rooms.event.domain.valueobjects.SeriesId;
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
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    /**
     * Liga esta ocorrência às irmãs geradas pela mesma regra. Nulo em evento avulso — a coluna
     * existe justamente para distinguir os dois casos sem tabela à parte.
     */
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "series_id"))
    private SeriesId seriesId;

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
            String recurrenceRule,
            SeriesId seriesId
    ) {
        this.id = new EventId();
        this.roomId = roomId;
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isAllDay = isAllDay;
        this.recurrenceRule = recurrenceRule;
        this.seriesId = seriesId;
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
            SeriesId seriesId,
            RoomOccupancy occupancy
    ) {
        Event event = new Event(
                roomId,
                title,
                description,
                startAt,
                endAt,
                isAllDay,
                recurrenceRule,
                seriesId
        );
        Notification notification = Notification.create();
        new EventValidation().validate(event, notification);
        notification.raiseIfHasErrors();
        event.requireFreeSlot(occupancy);
        return event;
    }

    /**
     * Não recebe {@code seriesId} de propósito: editar uma ocorrência não a tira da série. Uma
     * ocorrência que passa a divergir das irmãs continua sendo parte da mesma recorrência.
     */
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
     *
     * <p>Descartar não reescreve a agenda: apaga um registro que nunca deveria estar nela, e sem
     * isso uma aprovação indevida ficaria impossível de reverter.
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
        List<OccupiedSlot> occupants = occupancy.occupying(roomId, startAt, endAt).stream()
                .filter(other -> other.getStatus().occupiesRoom())
                .map(OccupiedSlot::of)
                .toList();
        List<EventConflict> conflicts = EventConflict.against(OccupiedSlot.of(this), occupants);
        if (!conflicts.isEmpty()) throw new EventConflictException(conflicts);
    }

    public EventId getId() {return id;}
    public RoomId getRoomId() {return roomId;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public LocalDateTime getStartAt() {return startAt;}
    public LocalDateTime getEndAt() {return endAt;}
    public Boolean isAllDay() {return isAllDay;}
    public String getRecurrenceRule() {return recurrenceRule;}
    public SeriesId getSeriesId() {return seriesId;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public Boolean getAllDay() {return isAllDay;}
    public EventStatus getStatus() {return status;}
}
