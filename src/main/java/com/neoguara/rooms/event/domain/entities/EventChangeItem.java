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

    private boolean oldIsAllDay;
    private boolean newIsAllDay;

    private String oldRecurrenceRule;
    private String newRecurrenceRule;

    EventChangeItem() {}

    public EventChangeItem (
        EventChangeRequestId eventChangeRequestId,
        RoomId roomId,
        String newTitle, String newDescription,
        LocalDateTime newStartAt, LocalDateTime newEndAt,
        Boolean newIsAllDay, String newRecurrenceRule
    ) {
        this.id = new EventChangeItemId();
        this.eventChangeRequestId = eventChangeRequestId;
        this.newRoomId = roomId;
        this.newTitle = newTitle;
        this.newDescription = newDescription;
        this.newStartAt = newStartAt;
        this.newEndAt = newEndAt;
        this.newIsAllDay = newIsAllDay;
        this.newRecurrenceRule = newRecurrenceRule;
    }

    public EventChangeItem(
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

    public EventChangeItemId getId() { return id; }

    public RoomId getOldRoomId() {return oldRoomId;}
    public RoomId getNewRoomId() {return newRoomId;}
    public boolean isOldIsAllDay() {return oldIsAllDay;}
    public boolean isNewIsAllDay() {return newIsAllDay;}
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
