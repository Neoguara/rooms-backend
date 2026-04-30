package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_change_items")
public class EventChangeItem {

    EventChangeItem() {}

    public static EventChangeItem createForNewEvent(EventChangeItemId id,
                                                    EventChangeRequestId eventChangeRequestId,
                                                    String newTitle, String newDescription,
                                                    LocalDateTime newStartAt, LocalDateTime newEndAt,
                                                    Boolean newIsAllDay, String newRecurrenceRule) {
        EventChangeItem item = new EventChangeItem();
        item.id = id;
        item.eventChangeRequestId = eventChangeRequestId;
        item.newTitle = newTitle;
        item.newDescription = newDescription;
        item.newStartAt = newStartAt;
        item.newEndAt = newEndAt;
        item.newIsAllDay = newIsAllDay;
        item.newRecurrenceRule = newRecurrenceRule;
        return item;
    }

    @EmbeddedId
    private EventChangeItemId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "event_change_request_id"))
    private EventChangeRequestId eventChangeRequestId;

    private String oldTitle;
    private String newTitle;

    private String oldDescription;
    private String newDescription;

    private LocalDateTime oldStartAt;
    private LocalDateTime newStartAt;

    private LocalDateTime oldEndAt;
    private LocalDateTime newEndAt;

    private Boolean oldIsAllDay = false;
    private Boolean newIsAllDay;

    private String oldRecurrenceRule;
    private String newRecurrenceRule;

    public EventChangeItemId getId() { return id; }
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
