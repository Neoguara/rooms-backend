package com.neoguara.rooms.event.application.dtos;

import com.neoguara.rooms.event.domain.services.EventConflict;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = """
        Choque de sala detectado na submissão. É um aviso, não uma recusa: o grupo foi criado e \
        segue para aprovação. A agenda pode mudar até lá — inclusive por outro item deste mesmo \
        grupo —, e a recusa definitiva só acontece na aprovação. Todos os campos são sempre \
        retornados.""")
public record EventConflictResponse(
        @Schema(description = "Descrição do choque em uma linha", requiredMode = Schema.RequiredMode.REQUIRED)
        String message,

        @Schema(description = "Título do que a alteração pretende marcar",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String attemptedTitle,

        @Schema(description = "Início pretendido", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime attemptedStartAt,

        @Schema(description = "Fim pretendido", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime attemptedEndAt,

        @Schema(description = "Título do que já segura a sala", requiredMode = Schema.RequiredMode.REQUIRED)
        String occupantTitle,

        @Schema(description = "Início de quem já segura a sala", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime occupantStartAt,

        @Schema(description = "Fim de quem já segura a sala", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime occupantEndAt
) {
    public static EventConflictResponse from(EventConflict conflict) {
        var attempted = conflict.attempted();
        var occupant = conflict.occupant();
        return new EventConflictResponse(
                conflict.describe(),
                attempted.title(), attempted.startAt(), attempted.endAt(),
                occupant.title(), occupant.startAt(), occupant.endAt()
        );
    }
}
