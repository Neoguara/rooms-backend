package com.neoguara.rooms.event.domain.services;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Agenda já carregada em memória. Existe para quem precisa consultar a ocupação muitas vezes
 * seguidas — a listagem de grupos pendentes, por exemplo — e prefere uma consulta larga a uma por
 * alteração.
 *
 * <p>Só enxerga o que foi carregado: quem monta precisa garantir que a janela cobre tudo o que será
 * perguntado, senão a resposta vem incompleta e o conflito passa despercebido.
 */
public final class LoadedOccupancy implements RoomOccupancy {

    private final List<Event> events;

    private LoadedOccupancy(List<Event> events) {
        this.events = List.copyOf(events);
    }

    public static RoomOccupancy of(List<Event> events) {
        return new LoadedOccupancy(events);
    }

    @Override
    public List<Event> occupying(RoomId roomId, LocalDateTime startAt, LocalDateTime endAt) {
        return events.stream()
                .filter(event -> event.getRoomId().equals(roomId))
                .filter(event -> event.getStartAt().isBefore(endAt) && event.getEndAt().isAfter(startAt))
                .toList();
    }
}
