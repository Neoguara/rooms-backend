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

    private Boolean isAllDay;

    private String recurrenceRule;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

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
        this.createdAt = LocalDateTime.now();
    }

    public static Event create(
            RoomId roomId,
            String title,
            String description,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Boolean isAllDay,
            String recurrenceRule
    ) {
        return new Event(roomId, title, description, startAt, endAt, isAllDay, recurrenceRule);
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
    public Boolean isAllDay() {
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

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void update(
            RoomId roomId,
            String title,
            String description,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Boolean isAllDay,
            String recurrenceRule
    ) {
        this.roomId = roomId;
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isAllDay = isAllDay;
        this.recurrenceRule = recurrenceRule;
        this.updatedAt = LocalDateTime.now();
    }

}
