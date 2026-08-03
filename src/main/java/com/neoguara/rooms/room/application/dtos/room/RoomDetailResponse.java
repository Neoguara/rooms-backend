package com.neoguara.rooms.room.application.dtos.room;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.neoguara.rooms.room.application.dtos.building.BuildingResponse;
import com.neoguara.rooms.room.application.dtos.resource.ResourceResponse;
import com.neoguara.rooms.room.application.dtos.roomtype.RoomTypeResponse;
import com.neoguara.rooms.room.domain.enums.RoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = """
        Dados de uma sala com campos expansíveis. Os campos `building`, `roomType` e `resources` \
        só são retornados quando solicitados via parâmetro `expand`; os demais são sempre retornados.""")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoomDetailResponse(
        @Schema(description = "ID da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,

        @Schema(description = "Nome da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Código identificador da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        String code,

        @Schema(description = "ID do tipo de sala", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID roomTypeId,

        @Schema(description = "ID do edifício onde a sala fica", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID buildingId,

        @Schema(description = "Andar da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        int floor,

        @Schema(description = "Capacidade da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        int capacity,

        @Schema(description = "Status atual da sala", requiredMode = Schema.RequiredMode.REQUIRED)
        RoomStatus status,

        @Schema(description = "Data de criação", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime updatedAt,

        @Schema(description = "Dados do edifício. Retornado apenas com `expand=building`",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        BuildingResponse building,

        @Schema(description = "Dados do tipo de sala. Retornado apenas com `expand=roomType`",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        RoomTypeResponse roomType,

        @Schema(description = "Recursos da sala. Retornado apenas com `expand=resources`",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        List<ResourceResponse> resources
) {}
