package com.neoguara.rooms.room.infrastructure.adapters;

import com.neoguara.rooms.report.application.ports.RoomReportDataPort;
import com.neoguara.rooms.room.application.ports.BuildingRepositoryPort;
import com.neoguara.rooms.room.application.ports.RoomRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Building;
import com.neoguara.rooms.room.domain.entities.Room;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Traduz id de sala no rótulo que aparece no relatório: nome, código e prédio.
 *
 * <p>Carrega salas e prédios de uma vez em vez de consultar por id. São duas consultas fixas contra
 * dezenas de registros, enquanto o caminho por id seria uma consulta por sala distinta do período —
 * o N+1 clássico, aqui dentro de um laço que já percorre a agenda inteira.
 */
@Component
public class RoomReportDataAdapter implements RoomReportDataPort {

    private final RoomRepositoryPort roomRepository;
    private final BuildingRepositoryPort buildingRepository;

    public RoomReportDataAdapter(RoomRepositoryPort roomRepository, BuildingRepositoryPort buildingRepository) {
        this.roomRepository = roomRepository;
        this.buildingRepository = buildingRepository;
    }

    @Override
    public Map<UUID, String> resolveRoomLabels(Collection<UUID> roomIds) {
        if (roomIds.isEmpty()) return Map.of();

        Set<UUID> wanted = new HashSet<>(roomIds);

        Map<UUID, String> buildingNames = buildingRepository.findAll().stream()
                .collect(Collectors.toMap(building -> building.getId().id(), Building::getName));

        return roomRepository.findAll().stream()
                .filter(room -> wanted.contains(room.getId().id()))
                .collect(Collectors.toMap(
                        room -> room.getId().id(),
                        room -> label(room, buildingNames),
                        // Ids são únicos, mas um merge explícito evita que uma duplicata inesperada
                        // derrube a geração inteira com IllegalStateException.
                        (first, second) -> first
                ));
    }

    private static String label(Room room, Map<UUID, String> buildingNames) {
        String base = room.getCode() != null
                ? "%s (%s)".formatted(room.getName(), room.getCode())
                : room.getName();

        String building = room.getBuildingId() != null
                ? buildingNames.get(room.getBuildingId().id())
                : null;

        return building != null ? base + " — " + building : base;
    }
}
