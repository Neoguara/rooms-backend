package com.neoguara.rooms.report.application.ports;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Traduz id de sala em algo legível, implementada pelo módulo {@code room}. Sem isso o relatório
 * sairia com UUID na coluna de sala, o que não serve para quem lê o arquivo.
 */
public interface RoomReportDataPort {

    /** Rótulos por id. Ids sem sala correspondente simplesmente não aparecem no mapa. */
    Map<UUID, String> resolveRoomLabels(Collection<UUID> roomIds);
}
