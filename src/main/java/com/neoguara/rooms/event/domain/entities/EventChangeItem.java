package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.enums.EventChangeItemStatus;
import com.neoguara.rooms.event.domain.enums.EventChangeType;
import com.neoguara.rooms.event.domain.validation.EventChangeItemValidation;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.valueobjects.EventSnapshot;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.*;

@Entity
@Table(name = "event_change_items")
public class EventChangeItem {
    @EmbeddedId
    private EventChangeItemId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "event_request_id"))
    private EventRequestId eventRequestId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "event_id"))
    private EventId eventId;

    @Enumerated(EnumType.STRING)
    private EventChangeType type;

    @Enumerated(EnumType.STRING)
    private EventChangeItemStatus status;

    /** Posição do item dentro do grupo, preservando a ordem em que foi submetido. */
    @Column(name = "item_position")
    private Integer position;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "roomId.id",       column = @Column(name = "old_room_id")),
        @AttributeOverride(name = "title",           column = @Column(name = "old_title")),
        @AttributeOverride(name = "description",     column = @Column(name = "old_description")),
        @AttributeOverride(name = "startAt",         column = @Column(name = "old_start_at")),
        @AttributeOverride(name = "endAt",           column = @Column(name = "old_end_at")),
        @AttributeOverride(name = "isAllDay",        column = @Column(name = "old_is_all_day")),
        @AttributeOverride(name = "recurrenceRule",  column = @Column(name = "old_recurrence_rule"))
    })
    private EventSnapshot before;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "roomId.id",       column = @Column(name = "new_room_id")),
        @AttributeOverride(name = "title",           column = @Column(name = "new_title")),
        @AttributeOverride(name = "description",     column = @Column(name = "new_description")),
        @AttributeOverride(name = "startAt",         column = @Column(name = "new_start_at")),
        @AttributeOverride(name = "endAt",           column = @Column(name = "new_end_at")),
        @AttributeOverride(name = "isAllDay",        column = @Column(name = "new_is_all_day")),
        @AttributeOverride(name = "recurrenceRule",  column = @Column(name = "new_recurrence_rule"))
    })
    private EventSnapshot after;

    EventChangeItem() {}

    private EventChangeItem(
            EventRequestId eventRequestId,
            int position,
            EventChangeType type,
            EventId eventId,
            EventSnapshot before,
            EventSnapshot after
    ) {
        this.id = new EventChangeItemId();
        this.eventRequestId = eventRequestId;
        this.position = position;
        this.type = type;
        this.eventId = eventId;
        this.status = EventChangeItemStatus.PENDING;
        this.before = before;
        this.after = after;
    }

    private static EventChangeItem validated(
            EventRequestId eventRequestId,
            int position,
            EventChangeType type,
            EventId eventId,
            EventSnapshot before,
            EventSnapshot after
    ) {
        EventChangeItem item = new EventChangeItem(eventRequestId, position, type, eventId, before, after);
        Notification notification = Notification.create();
        new EventChangeItemValidation().validate(item, notification);
        notification.raiseIfHasErrors();
        return item;
    }

    public static EventChangeItem create(EventRequestId eventRequestId, int position, EventSnapshot after) {
        return validated(eventRequestId, position, EventChangeType.CREATE, null, null, after);
    }

    public static EventChangeItem update(EventRequestId eventRequestId, int position, Event old, EventSnapshot after) {
        return validated(eventRequestId, position, EventChangeType.UPDATE, old.getId(), snapshotOf(old), after);
    }

    public static EventChangeItem cancel(EventRequestId eventRequestId, int position, Event event) {
        return validated(eventRequestId, position, EventChangeType.CANCEL, event.getId(), snapshotOf(event), null);
    }

    public static EventChangeItem reactivate(EventRequestId eventRequestId, int position, Event event) {
        return validated(eventRequestId, position, EventChangeType.REACTIVATE, event.getId(), snapshotOf(event), null);
    }

    public void approve() {
        if (this.status != EventChangeItemStatus.PENDING)
            throw new InvalidStateException("Only pending change items can be approved");
        this.status = EventChangeItemStatus.APPROVED;
    }

    public void reject() {
        if (this.status != EventChangeItemStatus.PENDING)
            throw new InvalidStateException("Only pending change items can be rejected");
        this.status = EventChangeItemStatus.REJECTED;
    }

    private static EventSnapshot snapshotOf(Event event) {
        return EventSnapshot.of(
            event.getRoomId(), event.getTitle(), event.getDescription(),
            event.getStartAt(), event.getEndAt(), event.isAllDay(), event.getRecurrenceRule()
        );
    }

    public EventChangeItemId getId() { return id; }
    public EventRequestId getEventRequestId() { return eventRequestId; }
    public Integer getPosition() { return position; }
    public EventId getEventId() { return eventId; }
    public EventChangeType getType() { return type; }
    public EventChangeItemStatus getStatus() { return status; }
    public EventSnapshot getBefore() { return before; }
    public EventSnapshot getAfter() { return after; }
}
