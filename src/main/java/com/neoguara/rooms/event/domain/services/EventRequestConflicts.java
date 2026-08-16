package com.neoguara.rooms.event.domain.services;

import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Antecipa, na submissão, os choques de sala que o grupo causaria se fosse aprovado agora.
 *
 * <p>É <strong>consultivo</strong>: não recusa nada. Um grupo pode legitimamente cancelar um evento
 * e reaproveitar o horário liberado no item seguinte, e barrar na submissão rejeitaria pedidos
 * válidos. Quem recusa é {@link com.neoguara.rooms.event.domain.entities.Event}, na aprovação, e o
 * estado da agenda pode muito bem ter mudado até lá.
 *
 * <p>Por isso a simulação percorre os itens <em>na ordem em que serão aplicados</em>, carregando o
 * que os anteriores liberaram e o que tomaram — sem isso, o cancelamento seguido de reaproveitamento
 * apareceria como conflito falso, e duas alterações sobrepostas dentro do mesmo grupo passariam
 * despercebidas.
 */
public final class EventRequestConflicts {

    private EventRequestConflicts() {}

    /** Tudo que os itens passariam a segurar. Serve para saber que janela da agenda carregar. */
    public static List<OccupiedSlot> claims(List<EventChangeItem> items) {
        return items.stream()
                .map(EventRequestConflicts::claimedBy)
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<EventConflict> preview(List<EventChangeItem> items, RoomOccupancy occupancy) {
        List<EventConflict> conflicts = new ArrayList<>();
        Set<UUID> released = new HashSet<>();
        List<OccupiedSlot> claimed = new ArrayList<>();

        for (EventChangeItem item : items) {
            releasedBy(item).ifPresent(released::add);

            OccupiedSlot slot = claimedBy(item);
            if (slot == null) continue;

            conflicts.addAll(EventConflict.against(slot, occupantsAround(slot, occupancy, released, claimed)));
            claimed.add(slot);
        }
        return conflicts;
    }

    /** A agenda real da janela, menos o que o grupo já liberou, mais o que o grupo já tomou. */
    private static List<OccupiedSlot> occupantsAround(
            OccupiedSlot slot,
            RoomOccupancy occupancy,
            Set<UUID> released,
            List<OccupiedSlot> claimed
    ) {
        List<OccupiedSlot> occupants = new ArrayList<>(
                occupancy.occupying(slot.roomId(), slot.startAt(), slot.endAt()).stream()
                        .filter(event -> event.getStatus().occupiesRoom())
                        .map(OccupiedSlot::of)
                        .filter(occupant -> !released.contains(occupant.id()))
                        .toList());
        occupants.addAll(claimed);
        return occupants;
    }

    /** O evento que o item deixa de segurar. Vazio nos itens que não liberam sala. */
    private static Optional<UUID> releasedBy(EventChangeItem item) {
        return switch (item.getType()) {
            case CANCEL, DISCARD, UPDATE -> Optional.of(item.getEventId().id());
            case CREATE, REACTIVATE -> Optional.empty();
        };
    }

    /**
     * A sala e o intervalo que o item passa a segurar, ou {@code null} nos itens que só liberam.
     * Um CREATE ainda não aprovado não tem evento, e responde pela identidade da própria alteração;
     * depois de aprovado passa a responder pelo evento que produziu, senão apareceria disputando a
     * sala com a própria criação.
     */
    private static OccupiedSlot claimedBy(EventChangeItem item) {
        return switch (item.getType()) {
            case CREATE -> slot(identityOf(item), item.getAfter());
            case UPDATE -> slot(item.getEventId().id(), item.getAfter());
            case REACTIVATE -> slot(item.getEventId().id(), item.getBefore());
            case CANCEL, DISCARD -> null;
        };
    }

    private static UUID identityOf(EventChangeItem item) {
        return item.getEventId() != null ? item.getEventId().id() : item.getId().id();
    }

    private static OccupiedSlot slot(UUID id, EventSnapshot snapshot) {
        return new OccupiedSlot(
                id, snapshot.getRoomId(), snapshot.getTitle(),
                snapshot.getStartAt(), snapshot.getEndAt());
    }
}
