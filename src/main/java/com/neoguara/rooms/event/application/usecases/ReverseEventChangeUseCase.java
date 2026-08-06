package com.neoguara.rooms.event.application.usecases;

import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.dtos.ReverseEventChangeRequest;
import com.neoguara.rooms.event.application.mappers.EventRequestMapper;
import com.neoguara.rooms.event.application.ports.EventChangeItemRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRepositoryPort;
import com.neoguara.rooms.event.application.ports.EventRequestRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.entities.EventRequest;
import com.neoguara.rooms.event.domain.enums.EventChangeItemStatus;
import com.neoguara.rooms.event.domain.valueobjects.EventId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import com.neoguara.rooms.event.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Abre um grupo novo contendo a alteração que desfaz a decisão tomada sobre um item. A alteração
 * inversa é derivada dos snapshots do item original, e o grupo gerado passa pela mesma aprovação
 * de qualquer outro: reverter é uma solicitação, não um atalho.
 */
@Service
public class ReverseEventChangeUseCase {

    private static final int SINGLE_ITEM = 0;

    private final EventRepositoryPort eventRepository;
    private final EventRequestRepositoryPort eventRequestRepository;
    private final EventChangeItemRepositoryPort changeItemRepository;

    ReverseEventChangeUseCase(
            EventRepositoryPort eventRepository,
            EventRequestRepositoryPort eventRequestRepository,
            EventChangeItemRepositoryPort changeItemRepository
    ) {
        this.eventRepository = eventRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.changeItemRepository = changeItemRepository;
    }

    @Transactional
    public EventRequestResponse execute(
            UUID eventRequestId,
            UUID itemId,
            UUID submittedBy,
            ReverseEventChangeRequest request
    ) {
        var requestId = EventRequestId.of(eventRequestId);

        eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Event request", eventRequestId));

        var original = changeItemRepository.findByEventRequestId(requestId).stream()
                .filter(item -> item.getId().id().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Change item in event request " + eventRequestId, itemId));

        if (original.getStatus() == EventChangeItemStatus.PENDING)
            throw new InvalidStateException(
                    "Only decided change items can be reversed. This one is still pending");

        if (changeItemRepository.hasReversal(original.getId()))
            throw new InvalidStateException("This change item has already been reversed");

        var reversalRequest = EventRequest.open(UserId.of(submittedBy), request.justification());
        var reversalItem = inverseOf(original, reversalRequest.getId());
        reversalItem.markAsReversalOf(original.getId());

        eventRequestRepository.save(reversalRequest);
        changeItemRepository.save(reversalItem);

        return EventRequestMapper.toResponse(reversalRequest, List.of(reversalItem));
    }

    /**
     * Deriva a alteração que desfaz o item. Uma rejeição é desfeita reenviando a alteração original;
     * uma aprovação, aplicando a operação contrária sobre o evento.
     */
    private EventChangeItem inverseOf(EventChangeItem original, EventRequestId groupId) {
        if (original.getStatus() == EventChangeItemStatus.REJECTED) {
            return switch (original.getType()) {
                case CREATE -> EventChangeItem.create(groupId, SINGLE_ITEM, original.getAfter());
                case UPDATE -> EventChangeItem.update(
                        groupId, SINGLE_ITEM, loadEvent(original.getEventId()), original.getAfter());
                case CANCEL -> EventChangeItem.cancel(groupId, SINGLE_ITEM, loadEvent(original.getEventId()));
                case REACTIVATE -> EventChangeItem.reactivate(
                        groupId, SINGLE_ITEM, loadEvent(original.getEventId()));
                case DISCARD -> EventChangeItem.discard(groupId, SINGLE_ITEM, loadEvent(original.getEventId()));
            };
        }

        return switch (original.getType()) {
            case CREATE -> EventChangeItem.discard(groupId, SINGLE_ITEM, loadEvent(original.getEventId()));
            case UPDATE -> EventChangeItem.update(
                    groupId, SINGLE_ITEM, loadEvent(original.getEventId()), original.getBefore());
            case CANCEL -> EventChangeItem.reactivate(groupId, SINGLE_ITEM, loadEvent(original.getEventId()));
            case REACTIVATE -> EventChangeItem.cancel(groupId, SINGLE_ITEM, loadEvent(original.getEventId()));
            case DISCARD -> throw new InvalidStateException(
                    "Discarded events cannot be restored. Submit a new CREATE change instead");
        };
    }

    private Event loadEvent(EventId eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId.id()));
    }
}
