package com.neoguara.rooms.event.infrastructure.adapters;

import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import com.neoguara.rooms.report.application.dtos.EventReportRow;
import com.neoguara.rooms.report.application.ports.EventReportDataPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entrega a agenda ao módulo {@code report} no formato que ele pede. A conversão para
 * {@link EventReportRow} acontece aqui, e não do outro lado, porque é o módulo dono do dado que
 * deve saber traduzi-lo — {@code report} nunca enxerga a entidade {@code Event}.
 */
@Component
public class EventReportDataAdapter implements EventReportDataPort {

    private final EventRepositoryPort eventRepository;

    public EventReportDataAdapter(EventRepositoryPort eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<EventReportRow> findForReport(LocalDateTime startAt, LocalDateTime endAt) {
        return toRows(eventRepository.findExistingBetween(startAt, endAt));
    }

    @Override
    public List<EventReportRow> findForReportByRoom(UUID roomId, LocalDateTime startAt, LocalDateTime endAt) {
        return toRows(eventRepository.findExistingBetweenByRoom(RoomId.of(roomId), startAt, endAt));
    }

    private static List<EventReportRow> toRows(List<Event> events) {
        return events.stream().map(EventReportDataAdapter::toRow).toList();
    }

    private static EventReportRow toRow(Event event) {
        return new EventReportRow(
                event.getId().id(),
                event.getRoomId().id(),
                event.getTitle(),
                event.getStartAt(),
                event.getEndAt(),
                event.isAllDay(),
                event.getStatus().name(),
                event.getSeriesId() != null ? event.getSeriesId().id() : null
        );
    }
}
