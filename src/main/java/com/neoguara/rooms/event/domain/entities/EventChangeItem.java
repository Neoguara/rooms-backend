package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_change_items")
public class EventChangeItem {
    @EmbeddedId
    private EventChangeItemId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "event_change_request_id"))
    private EventChangeRequestId eventChangeRequestId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "old_room_id"))
    private RoomId oldRoomId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "new_room_id"))
    private RoomId newRoomId;

    private String oldTitle;
    private String newTitle;

    private String oldDescription;
    private String newDescription;

    private LocalDateTime oldStartAt;
    private LocalDateTime newStartAt;

    private LocalDateTime oldEndAt;
    private LocalDateTime newEndAt;

    private Boolean oldIsAllDay;
    private Boolean newIsAllDay;

    private String oldRecurrenceRule;
    private String newRecurrenceRule;

    EventChangeItem() {}

    private EventChangeItem(
        EventChangeRequestId eventChangeRequestId,
        RoomId oldRoomId, RoomId newRoomId,
        String oldTitle, String newTitle,
        String oldDescription, String newDescription,
        LocalDateTime oldStartAt, LocalDateTime newStartAt,
        LocalDateTime oldEndAt, LocalDateTime newEndAt,
        Boolean oldIsAllDay, Boolean newIsAllDay,
        String oldRecurrenceRule, String newRecurrenceRule
    ) {
        this.id = new EventChangeItemId();
        this.eventChangeRequestId = eventChangeRequestId;
        this.oldRoomId = oldRoomId;
        this.newRoomId = newRoomId;
        this.oldTitle = oldTitle;
        this.newTitle = newTitle;
        this.oldDescription = oldDescription;
        this.newDescription = newDescription;
        this.oldStartAt = oldStartAt;
        this.newStartAt = newStartAt;
        this.oldEndAt = oldEndAt;
        this.newEndAt = newEndAt;
        this.oldIsAllDay = oldIsAllDay;
        this.newIsAllDay = newIsAllDay;
        this.oldRecurrenceRule = oldRecurrenceRule;
        this.newRecurrenceRule = newRecurrenceRule;
    }

    public static EventChangeItem create(
        EventChangeRequestId eventChangeRequestId,
        RoomId roomId,
        String title, String description,
        LocalDateTime startAt, LocalDateTime endAt,
        Boolean isAllDay, String recurrenceRule
    ) {
        return new EventChangeItem(
            eventChangeRequestId,
            null, roomId,
            null, title,
            null, description,
            null, startAt,
            null, endAt,
            null, isAllDay,
            null, recurrenceRule
        );
    }

    public static EventChangeItem update(
        EventChangeRequestId eventChangeRequestId,
        RoomId oldRoomId, RoomId newRoomId,
        String oldTitle, String newTitle,
        String oldDescription, String newDescription,
        LocalDateTime oldStartAt, LocalDateTime newStartAt,
        LocalDateTime oldEndAt, LocalDateTime newEndAt,
        Boolean oldIsAllDay, Boolean newIsAllDay,
        String oldRecurrenceRule, String newRecurrenceRule
    ) {
        return new EventChangeItem(
            eventChangeRequestId,
            oldRoomId, newRoomId,
            oldTitle, newTitle,
            oldDescription, newDescription,
            oldStartAt, newStartAt,
            oldEndAt, newEndAt,
            oldIsAllDay, newIsAllDay,
            oldRecurrenceRule, newRecurrenceRule
        );
    }

    public static EventChangeItem delete(
        EventChangeRequestId eventChangeRequestId,
        RoomId roomId,
        String title, String description,
        LocalDateTime startAt, LocalDateTime endAt,
        Boolean isAllDay, String recurrenceRule
    ) {
        return new EventChangeItem(
            eventChangeRequestId,
            roomId, null,
            title, null,
            description, null,
            startAt, null,
            endAt, null,
            isAllDay, null,
            recurrenceRule, null
        );
    }

    public EventChangeItemId getId() { return id; }
    public RoomId getOldRoomId() { return oldRoomId; }
    public RoomId getNewRoomId() { return newRoomId; }
    public EventChangeRequestId getEventChangeRequestId() { return eventChangeRequestId; }
    public String getOldTitle() { return oldTitle; }
    public String getNewTitle() { return newTitle; }
    public String getOldDescription() { return oldDescription; }
    public String getNewDescription() { return newDescription; }
    public LocalDateTime getOldStartAt() { return oldStartAt; }
    public LocalDateTime getNewStartAt() { return newStartAt; }
    public LocalDateTime getOldEndAt() { return oldEndAt; }
    public LocalDateTime getNewEndAt() { return newEndAt; }
    public Boolean getOldIsAllDay() { return oldIsAllDay; }
    public Boolean getNewIsAllDay() { return newIsAllDay; }
    public String getOldRecurrenceRule() { return oldRecurrenceRule; }
    public String getNewRecurrenceRule() { return newRecurrenceRule; }

}
