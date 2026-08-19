package com.neoguara.rooms.event.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Alcance de uma alteração sobre um evento que faz parte de uma série recorrente. Omitido, \
        vale `THIS_OCCURRENCE`, que é o comportamento de um evento avulso. Os alcances em lote \
        exigem que o evento pertença a uma série, e são expandidos ainda na submissão: o grupo já \
        nasce com um item por ocorrência atingida, e quem aprova vê exatamente quais são.""")
public enum ChangeScope {

    @Schema(description = "Só o evento informado")
    THIS_OCCURRENCE,

    @Schema(description = "O evento informado e as ocorrências seguintes da mesma série")
    THIS_AND_FOLLOWING,

    @Schema(description = "Todas as ocorrências da série, inclusive as anteriores")
    ALL_OCCURRENCES
}
