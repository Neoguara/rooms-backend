package com.neoguara.rooms.event.domain.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.time.LocalDateTime;

@Embeddable
public class EventSnapshot {
    @Embedded
    private RoomId roomId;
    private String title;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Boolean isAllDay;
    private String recurrenceRule;

    @Embedded
    private SeriesId seriesId;

    EventSnapshot() {}

    private EventSnapshot(RoomId roomId, String title, String description,
                          LocalDateTime startAt, LocalDateTime endAt,
                          Boolean isAllDay, String recurrenceRule, SeriesId seriesId) {
        this.roomId = roomId;
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isAllDay = isAllDay;
        this.recurrenceRule = recurrenceRule;
        this.seriesId = seriesId;
    }

    public static EventSnapshot of(RoomId roomId, String title, String description,
                                   LocalDateTime startAt, LocalDateTime endAt,
                                   Boolean isAllDay, String recurrenceRule, SeriesId seriesId) {
        return new EventSnapshot(roomId, title, description, startAt, endAt, isAllDay, recurrenceRule, seriesId);
    }

    public RoomId getRoomId() { return roomId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public Boolean isAllDay() { return isAllDay; }
    public String getRecurrenceRule() { return recurrenceRule; }
    public SeriesId getSeriesId() { return seriesId; }
}
