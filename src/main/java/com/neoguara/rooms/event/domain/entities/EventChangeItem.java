package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.validation.EventChangeItemValidation;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.valueobjects.EventSnapshot;
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

    private EventChangeItem(EventRequestId eventRequestId, EventSnapshot before, EventSnapshot after) {
        this.id = new EventChangeItemId();
        this.eventRequestId = eventRequestId;
        this.before = before;
        this.after = after;
    }

    private static EventChangeItem validated(EventRequestId eventRequestId, EventSnapshot before, EventSnapshot after) {
        EventChangeItem item = new EventChangeItem(eventRequestId, before, after);
        Notification notification = Notification.create();
        new EventChangeItemValidation().validate(item, notification);
        notification.raiseIfHasErrors();
        return item;
    }

    public static EventChangeItem create(EventRequestId eventRequestId, EventSnapshot after) {
        return validated(eventRequestId, null, after);
    }

    public static EventChangeItem update(EventRequestId eventRequestId, Event old, EventSnapshot after) {
        return validated(eventRequestId, snapshotOf(old), after);
    }

    public static EventChangeItem cancel(EventRequestId eventRequestId, Event event) {
        return validated(eventRequestId, snapshotOf(event), null);
    }

    private static EventSnapshot snapshotOf(Event event) {
        return EventSnapshot.of(
            event.getRoomId(), event.getTitle(), event.getDescription(),
            event.getStartAt(), event.getEndAt(), event.isAllDay(), event.getRecurrenceRule()
        );
    }

    public EventChangeItemId getId() { return id; }
    public EventRequestId getEventRequestId() { return eventRequestId; }
    public EventSnapshot getBefore() { return before; }
    public EventSnapshot getAfter() { return after; }
}
