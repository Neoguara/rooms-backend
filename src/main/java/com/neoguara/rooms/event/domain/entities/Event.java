package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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

    private boolean isAllDay;

    private String recurrenceRule;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    Event () {}

    public Event(
            RoomId roomId,
            String title,
            String description,
            LocalDateTime startAt,
            LocalDateTime endAt,
            boolean isAllDay,
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
        this.createdAt = LocalDateTime.now();
    }

    public EventId getId() {
        return id;
    }
    public RoomId getRoomId() {return roomId;}
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public LocalDateTime getStartAt() {
        return startAt;
    }
    public LocalDateTime getEndAt() {
        return endAt;
    }
    public boolean isAllDay() {
        return isAllDay;
    }
    public String getRecurrenceRule() {
        return recurrenceRule;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

}
