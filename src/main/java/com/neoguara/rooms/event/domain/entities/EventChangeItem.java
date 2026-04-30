package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeRequestId;
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

    private String OldRecurrenceRule;
    private String newRecurrenceRule;


}
