package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.dtos.ReverseEventRequest;
import com.neoguara.rooms.event.application.mappers.EventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.enums.EventRequestStatus;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Abre um grupo novo contendo as alterações que desfazem a decisão tomada sobre outro grupo. As
 * alterações inversas são derivadas dos snapshots dos itens originais, e o grupo gerado passa pela
 * mesma aprovação de qualquer outro: reverter é uma solicitação, não um atalho.
 */
@Service
public class ReverseEventRequestUseCase {

    private final EventRepositoryPort eventRepository;
    private final EventRequestRepositoryPort eventRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;

    ReverseEventRequestUseCase(
            EventRepositoryPort eventRepository,
            EventRequestRepositoryPort eventRequestRepository,
            EventChangeItemRepositoryPort changeItemRepository
    ) {
        this.eventRepository = eventRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.changeItemRepository = changeItemRepository;
    }

    @Transactional
    public EventRequestResponse execute(UUID eventRequestId, UUID submittedBy, ReverseEventRequest request) {
        var requestId = EventRequestId.of(eventRequestId);

        var original = eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Event request", eventRequestId));

        if (original.getStatus() == EventRequestStatus.PENDING)
            throw new InvalidStateException(
                    "Only decided event requests can be reversed. This one is still pending");

        if (eventRequestRepository.hasReversal(requestId))
            throw new InvalidStateException("This event request has already been reversed");

        var reversal = EventRequest.open(UserId.of(submittedBy), request.justification());
        reversal.markAsReversalOf(requestId);

        var reversalItems = inverseOf(original, changeItemRepository.findByEventRequestId(requestId),
                reversal.getId());

        eventRequestRepository.save(reversal);
        changeItemRepository.saveAll(reversalItems);

        return EventRequestMapper.toResponse(reversal, reversalItems);
    }

    /**
     * Deriva as alterações que desfazem o grupo. Uma rejeição é desfeita reenviando as alterações
     * originais, na mesma ordem; uma aprovação, aplicando a operação contrária de cada item na
     * ordem inversa — desfazer um grupo é desfazer o que ele fez por último primeiro.
     */
    private List<EventChangeItem> inverseOf(
            EventRequest original,
            List<EventChangeItem> originalItems,
            EventRequestId groupId
    ) {
        boolean wasApproved = original.getStatus() == EventRequestStatus.APPROVED;
        List<EventChangeItem> source = wasApproved ? originalItems.reversed() : originalItems;

        return IntStream.range(0, source.size())
                .mapToObj(position -> {
                    var originalItem = source.get(position);
                    var reversalItem = wasApproved
                            ? undo(originalItem, groupId, position)
                            : resubmit(originalItem, groupId, position);
                    reversalItem.markAsReversalOf(originalItem.getId());
                    return reversalItem;
                })
                .toList();
    }

    /** A operação contrária à que o item efetivou sobre o evento. */
    private EventChangeItem undo(EventChangeItem item, EventRequestId groupId, int position) {
        return switch (item.getType()) {
            case CREATE -> EventChangeItem.discard(groupId, position, loadEvent(item.getEventId()));
            case UPDATE -> EventChangeItem.update(
                    groupId, position, loadEvent(item.getEventId()), item.getBefore());
            case CANCEL -> EventChangeItem.reactivate(groupId, position, loadEvent(item.getEventId()));
            case REACTIVATE -> EventChangeItem.cancel(groupId, position, loadEvent(item.getEventId()));
            case DISCARD -> throw new InvalidStateException(
                    "Discarded events cannot be restored. Submit a new CREATE change instead");
        };
    }

    /** A mesma alteração de novo, para que a rejeição possa ser reconsiderada. */
    private EventChangeItem resubmit(EventChangeItem item, EventRequestId groupId, int position) {
        return switch (item.getType()) {
            case CREATE -> EventChangeItem.create(groupId, position, item.getAfter());
            case UPDATE -> EventChangeItem.update(
                    groupId, position, loadEvent(item.getEventId()), item.getAfter());
            case CANCEL -> EventChangeItem.cancel(groupId, position, loadEvent(item.getEventId()));
            case REACTIVATE -> EventChangeItem.reactivate(groupId, position, loadEvent(item.getEventId()));
            case DISCARD -> EventChangeItem.discard(groupId, position, loadEvent(item.getEventId()));
        };
    }

    private Event loadEvent(EventId eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId.id()));
    }
}
