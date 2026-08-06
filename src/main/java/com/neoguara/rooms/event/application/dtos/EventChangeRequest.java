package com.neoguara.rooms.event.application.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Uma alteração solicitada sobre um evento. O campo `type` define o formato do objeto: \
        `CREATE` descreve um evento novo e não aceita `eventId`; `UPDATE` exige `eventId` e o estado \
        completo desejado do evento; `CANCEL` e `REACTIVATE` exigem apenas `eventId`.""",
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateEventChange.class, name = "CREATE"),
        @JsonSubTypes.Type(value = UpdateEventChange.class, name = "UPDATE"),
        @JsonSubTypes.Type(value = CancelEventChange.class, name = "CANCEL"),
        @JsonSubTypes.Type(value = ReactivateEventChange.class, name = "REACTIVATE")
})
public sealed interface EventChangeRequest
        permits CreateEventChange, UpdateEventChange, CancelEventChange, ReactivateEventChange {
}
