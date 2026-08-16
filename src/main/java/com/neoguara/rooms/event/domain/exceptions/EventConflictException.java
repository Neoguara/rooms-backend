package com.neoguara.rooms.event.domain.exceptions;

import com.neoguara.rooms.event.domain.services.EventConflict;
import com.neoguara.rooms.shared.domain.exceptions.ConflictException;

import java.util.List;

/**
 * Recusa um evento porque a sala já está ocupada no horário pretendido. Carrega todos os choques
 * encontrados, e não apenas o primeiro: quando um grupo de alterações mexe em várias datas, saber
 * só que "houve conflito" não diz o que corrigir.
 */
public class EventConflictException extends ConflictException {

    private final transient List<EventConflict> conflicts;

    public EventConflictException(List<EventConflict> conflicts) {
        super(conflicts.stream().map(EventConflict::describe).toList());
        this.conflicts = List.copyOf(conflicts);
    }

    public List<EventConflict> getConflicts() {
        return conflicts;
    }
}
