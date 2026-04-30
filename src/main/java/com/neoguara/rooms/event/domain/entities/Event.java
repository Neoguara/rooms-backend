package com.neoguara.rooms.event.domain.entities;

import com.neoguara.rooms.event.domain.valueobjects.EventId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
    @EmbeddedId
    private EventId id;

    private String title;

    private String description;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private boolean isAllDay;

    private String recurrenceRule;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

}
