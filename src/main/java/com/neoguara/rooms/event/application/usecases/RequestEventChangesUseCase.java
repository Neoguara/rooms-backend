package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.CancelEventChange;
import com.neoguara.rooms.event.application.dtos.ChangeScope;
import com.neoguara.rooms.event.application.dtos.CreateEventChange;
import com.neoguara.rooms.event.application.dtos.EventChangeRequest;
import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.dtos.ReactivateEventChange;
import com.neoguara.rooms.event.application.dtos.SubmitEventRequest;
import com.neoguara.rooms.event.application.dtos.UpdateEventChange;
import com.neoguara.rooms.event.application.mappers.EventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.enums.EventStatus;
import com.neoguara.rooms.event.domain.services.EventRequestConflicts;
import com.neoguara.rooms.event.domain.services.OccurrenceShift;
import com.neoguara.rooms.event.domain.services.RecurrenceExpander;
import com.neoguara.rooms.event.domain.services.RoomOccupancy;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.valueobjects.EventSnapshot;
import com.neoguara.rooms.event.domain.valueobjects.RecurrenceRule;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;
import com.neoguara.rooms.event.domain.valueobjects.SeriesId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Registra um grupo de alterações a decidir. Uma alteração submetida pode virar <em>mais de um</em>
 * item: criar um evento recorrente vira um item por ocorrência, e cancelar uma série inteira vira um
 * item por ocorrência atingida.
 *
 * <p>A expansão acontece aqui, e não na aprovação, porque é o que mantém a promessa do grupo: quem
 * decide vê exatamente quais eventos serão tocados, e a reversão e o aviso de conflito continuam
 * funcionando item a item, sem saber que recorrência existe.
 */
@Service
public class RequestEventChangesUseCase {

    private final EventRepositoryPort eventRepository;
    private final EventRequestRepositoryPort eventRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;
    private final RoomOccupancy roomOccupancy;

    public RequestEventChangesUseCase(
            EventRepositoryPort eventRepository,
            EventRequestRepositoryPort eventRequestRepository,
            EventChangeItemRepositoryPort changeItemRepository,
            RoomOccupancy roomOccupancy
    ) {
        this.eventRepository = eventRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.changeItemRepository = changeItemRepository;
        this.roomOccupancy = roomOccupancy;
    }

    @Transactional
    public EventRequestResponse execute(UUID submittedBy, SubmitEventRequest request) {
        Notification notification = Notification.create();
        notification.addErrorIf(
                request.changes() == null || request.changes().isEmpty(),
                "changes must contain at least one item"
        );
        notification.raiseIfHasErrors();

        var eventRequest = EventRequest.open(UserId.of(submittedBy), request.justification());

        List<EventChangeItem> changeItems = new ArrayList<>();
        for (EventChangeRequest change : request.changes()) {
            changeItems.addAll(toChangeItems(eventRequest.getId(), changeItems.size(), change));
        }

        eventRequestRepository.save(eventRequest);
        changeItemRepository.saveAll(changeItems);

        return EventRequestMapper.toResponse(
                eventRequest, changeItems, EventRequestConflicts.preview(changeItems, roomOccupancy));
    }

    /** @param position posição do primeiro item gerado; os demais seguem em sequência. */
    private List<EventChangeItem> toChangeItems(
            EventRequestId eventRequestId, int position, EventChangeRequest change) {
        return switch (change) {
            case CreateEventChange c -> createItems(eventRequestId, position, c);
            case UpdateEventChange u -> updateItems(eventRequestId, position, u);
            case CancelEventChange c -> scopedItems(
                    eventRequestId, position, c.eventId(), c.scope(),
                    EventStatus.ACTIVE, EventChangeItem::cancel);
            case ReactivateEventChange r -> scopedItems(
                    eventRequestId, position, r.eventId(), r.scope(),
                    EventStatus.CANCELLED, EventChangeItem::reactivate);
        };
    }

    /**
     * Sem regra de recorrência, um item só, como sempre foi. Com regra, uma ocorrência por item,
     * todas amarradas pelo mesmo {@link SeriesId} e guardando a regra na forma canônica.
     */
    private List<EventChangeItem> createItems(
            EventRequestId eventRequestId, int position, CreateEventChange change) {
        if (change.recurrenceRule() == null || change.recurrenceRule().isBlank()) {
            return List.of(EventChangeItem.create(eventRequestId, position, snapshotOf(
                    change.roomId(), change.title(), change.description(),
                    change.startAt(), change.endAt(), change.isAllDay(), null, null)));
        }

        RecurrenceRule rule = RecurrenceRule.parse(change.recurrenceRule());
        List<RecurrenceExpander.Occurrence> occurrences =
                RecurrenceExpander.expand(rule, change.startAt(), change.endAt());

        Notification.create()
                .addErrorIf(occurrences.isEmpty(),
                        "recurrenceRule produces no occurrence at or after startAt")
                .raiseIfHasErrors();

        SeriesId seriesId = new SeriesId();
        String canonicalRule = rule.format();

        return IntStream.range(0, occurrences.size())
                .mapToObj(index -> {
                    var occurrence = occurrences.get(index);
                    return EventChangeItem.create(eventRequestId, position + index, snapshotOf(
                            change.roomId(), change.title(), change.description(),
                            occurrence.startAt(), occurrence.endAt(), change.isAllDay(),
                            canonicalRule, seriesId));
                })
                .toList();
    }

    /**
     * A série do evento é preservada: editar uma ocorrência não a tira da recorrência. Em lote, o
     * horário informado vale como deslocamento a partir do evento de referência — é o que permite
     * mover uma série inteira de um dia da semana para outro.
     */
    private List<EventChangeItem> updateItems(
            EventRequestId eventRequestId, int position, UpdateEventChange change) {
        Event reference = loadEvent(change.eventId());
        requireUnchangedRecurrenceRule(reference, change.recurrenceRule());

        SeriesTargets targets = targetsOf(reference, change.scope(), EventStatus.ACTIVE);

        if (targets.isSingleOccurrence()) {
            return List.of(EventChangeItem.update(eventRequestId, position, reference, snapshotOf(
                    change.roomId(), change.title(), change.description(),
                    change.startAt(), change.endAt(), change.isAllDay(),
                    reference.getRecurrenceRule(), reference.getSeriesId())));
        }

        OccurrenceShift shift =
                OccurrenceShift.between(reference.getStartAt(), change.startAt(), change.endAt());
        String rule = ruleAfter(reference, targets, shift);

        return IntStream.range(0, targets.applicable().size())
                .mapToObj(index -> {
                    Event occurrence = targets.applicable().get(index);
                    return EventChangeItem.update(eventRequestId, position + index, occurrence, snapshotOf(
                            change.roomId(), change.title(), change.description(),
                            shift.startFrom(occurrence.getStartAt()),
                            shift.endFrom(occurrence.getStartAt()),
                            change.isAllDay(), rule, occurrence.getSeriesId()));
                })
                .toList();
    }

    /**
     * A regra não é editável por {@code UPDATE}: ela descreve a série, e trocá-la sem regerar as
     * datas devolveria o texto ao estado de mentira que a recorrência veio corrigir. Omitir o campo
     * é aceito e significa "mantenha".
     */
    private void requireUnchangedRecurrenceRule(Event event, String submitted) {
        if (submitted == null || submitted.isBlank()) return;
        if (!submitted.equals(event.getRecurrenceRule()))
            throw new InvalidStateException(
                    "recurrenceRule cannot be changed by an UPDATE. Cancel the remaining occurrences "
                            + "and create a new series instead");
    }

    /**
     * Reescreve o BYDAY com os dias que a série passa a ocupar — mas só quando o deslocamento move
     * a série <em>inteira</em>. Movendo uma parte, nenhuma RRULE única descreveria o resultado, e
     * gravar um BYDAY que contradiz metade das ocorrências seria pior que manter o padrão de origem.
     */
    private String ruleAfter(Event reference, SeriesTargets targets, OccurrenceShift shift) {
        String current = reference.getRecurrenceRule();
        if (current == null || !targets.coversWholeSeries()) return current;

        RecurrenceRule rule = RecurrenceRule.parse(current);
        if (rule.byDay().isEmpty()) return current;

        Set<DayOfWeek> days = targets.applicable().stream()
                .map(occurrence -> shift.startFrom(occurrence.getStartAt()).getDayOfWeek())
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(DayOfWeek.class)));

        return new RecurrenceRule(rule.frequency(), rule.interval(), days, rule.count(), rule.until())
                .format();
    }

    private List<EventChangeItem> scopedItems(
            EventRequestId eventRequestId,
            int position,
            UUID eventId,
            ChangeScope scope,
            EventStatus applicableTo,
            ItemFactory factory
    ) {
        List<Event> targets = targetsOf(loadEvent(eventId), scope, applicableTo).applicable();
        return IntStream.range(0, targets.size())
                .mapToObj(index -> factory.of(eventRequestId, position + index, targets.get(index)))
                .toList();
    }

    /**
     * Resolve quais eventos o alcance atinge. Em lote, ocorrências fora do estado exigido são
     * descartadas: o grupo vale todo ou nada, e uma ocorrência cancelada antes, em separado, não
     * pode derrubar a operação sobre o resto da série. Ocorrências já terminadas seguem no lote —
     * evento vencido também aceita alteração. Em {@code THIS_OCCURRENCE} nada é filtrado: ali o
     * pedido é sobre aquele evento, e um estado incompatível deve falhar na aprovação, como sempre.
     */
    private SeriesTargets targetsOf(Event event, ChangeScope scope, EventStatus applicableTo) {
        ChangeScope effective = scope == null ? ChangeScope.THIS_OCCURRENCE : scope;
        if (effective == ChangeScope.THIS_OCCURRENCE) return SeriesTargets.single(event);

        if (event.getSeriesId() == null)
            throw new InvalidStateException(
                    "Scope " + effective + " requires an event that belongs to a recurring series");

        List<Event> series = eventRepository.findBySeriesId(event.getSeriesId());
        List<Event> applicable = series.stream()
                .filter(occurrence -> effective == ChangeScope.ALL_OCCURRENCES
                        || !occurrence.getStartAt().isBefore(event.getStartAt()))
                .filter(occurrence -> occurrence.getStatus() == applicableTo)
                .toList();

        if (applicable.isEmpty())
            throw new InvalidStateException(
                    "No occurrence in scope " + effective + " is in state " + applicableTo);

        return new SeriesTargets(effective, series, applicable);
    }

    /**
     * O que o alcance atingiu, junto do tamanho da série, que é o que diz se a regra de recorrência
     * ainda descreve todas as ocorrências depois da alteração.
     */
    private record SeriesTargets(ChangeScope scope, List<Event> series, List<Event> applicable) {
        static SeriesTargets single(Event event) {
            return new SeriesTargets(ChangeScope.THIS_OCCURRENCE, List.of(event), List.of(event));
        }

        boolean isSingleOccurrence() {
            return scope == ChangeScope.THIS_OCCURRENCE;
        }

        boolean coversWholeSeries() {
            return applicable.size() == series.size();
        }
    }

    private Event loadEvent(UUID eventId) {
        return eventRepository.findById(EventId.of(eventId))
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
    }

    private static EventSnapshot snapshotOf(
            UUID roomId, String title, String description,
            LocalDateTime startAt, LocalDateTime endAt,
            Boolean isAllDay, String recurrenceRule, SeriesId seriesId
    ) {
        return EventSnapshot.of(
                RoomId.of(roomId), title, description, startAt, endAt, isAllDay, recurrenceRule, seriesId);
    }

    /** Assinatura comum dos factories de {@link EventChangeItem} que só precisam do evento. */
    @FunctionalInterface
    private interface ItemFactory {
        EventChangeItem of(EventRequestId eventRequestId, int position, Event event);
    }
}
