package com.neoguara.rooms.event.domain.services;

import com.neoguara.rooms.event.domain.entities.Event;
import com.neoguara.rooms.event.domain.valueobjects.RoomId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Enxerga quem já segura uma sala. Existe para que {@link Event} possa recusar um horário ocupado
 * sem conhecer repositório algum: quem chama é obrigado a entregar essa capacidade, e é essa
 * obrigação — visível na assinatura dos métodos — que impede um caso de uso novo de esquecer a
 * checagem.
 *
 * <p>Mora no domínio, e não em {@code application.ports}, porque quem depende dela é a entidade.
 */
@FunctionalInterface
public interface RoomOccupancy {

    /**
     * Eventos que seguram {@code roomId} em algum ponto de [{@code startAt}, {@code endAt}).
     * Pode incluir o próprio evento que está sendo verificado.
     */
    List<Event> occupying(RoomId roomId, LocalDateTime startAt, LocalDateTime endAt);
}
