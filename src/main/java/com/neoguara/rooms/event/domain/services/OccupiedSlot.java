package com.neoguara.rooms.event.domain.services;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Uma sala presa por um intervalo. É a unidade de comparação da regra de conflito, e existe para
 * que a mesma regra sirva tanto a um evento já gravado quanto a uma alteração ainda pendente de
 * aprovação — que não tem evento nenhum por trás.
 *
 * @param id identidade de quem segura a sala: o evento, quando ele existe; a alteração que o
 *           criará, quando ainda não existe. Serve só para um slot não disputar consigo mesmo.
 */
public record OccupiedSlot(
        UUID id,
        RoomId roomId,
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
    public static OccupiedSlot of(Event event) {
        return new OccupiedSlot(
                event.getId().id(), event.getRoomId(), event.getTitle(),
                event.getStartAt(), event.getEndAt());
    }

    /**
     * Intervalo semiaberto: dois slots colados, em que um termina exatamente quando o outro começa,
     * não disputam a sala.
     */
    public boolean competesWith(OccupiedSlot other) {
        return !id.equals(other.id)
                && roomId.equals(other.roomId)
                && startAt.isBefore(other.endAt)
                && endAt.isAfter(other.startAt);
    }
}
