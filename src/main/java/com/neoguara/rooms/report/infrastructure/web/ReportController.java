package com.neoguara.rooms.report.infrastructure.web;

import com.neoguara.rooms.auth.AuthUserDetails;
import com.neoguara.rooms.report.application.dtos.ReportDownload;
import com.neoguara.rooms.report.application.dtos.ReportResponse;
import com.neoguara.rooms.report.application.dtos.SubmitReportRequest;
import com.neoguara.rooms.report.application.usecases.DownloadReportUseCase;
import com.neoguara.rooms.report.application.usecases.GetReportUseCase;
import com.neoguara.rooms.report.application.usecases.SubmitReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Tag(name = "Reports", description = """
        Relatórios não são gerados durante a requisição: pedir e receber são passos separados, \
        porque apurar um intervalo grande demora mais do que uma resposta HTTP deveria.

        ## Ciclo de vida

        1. `POST /reports` registra o pedido e responde `202` com o id. O relatório nasce `PENDING`.
        2. `GET /reports/{id}` informa em que pé está. O cliente consulta até `status` sair de \
        `PENDING`/`PROCESSING`.
        3. `GET /reports/{id}/download` entrega o arquivo, disponível apenas em `COMPLETED`.

        `GET /reports` devolve a lista completa, do pedido mais recente para o mais antigo.

        ## Estados

        | Status | Significa |
        |---|---|
        | `PENDING` | na fila, geração ainda não começou |
        | `PROCESSING` | sendo gerado |
        | `COMPLETED` | pronto; `downloadUrl` preenchido |
        | `FAILED` | a geração falhou; o motivo está em `failureReason` |
        | `EXPIRED` | concluído no passado, arquivo já descartado |

        Os estados não voltam atrás e não há reprocessamento: um relatório que falhou continua \
        falhado, e tentar de novo é submeter outro pedido. Isso mantém cada arquivo amarrado às \
        condições em que foi gerado.

        ## Quem enxerga o quê

        `GET /reports` lista os relatórios de **todos** os usuários, porque expõe apenas \
        metadados: tipo, período, estado e autor.

        O conteúdo é outra história. Consultar `GET /reports/{id}` ou baixar o arquivo de outra \
        pessoa responde `404`, e não `403` — um `403` já confirmaria que aquele id existe. \
        Ver a fila inteira é uma coisa; levar o arquivo alheio é outra.

        ## Relatório vazio não é erro

        Um intervalo sem nenhum evento conclui normalmente, com arquivo contendo só o cabeçalho. \
        Não achar nada é resposta legítima da agenda, não falha da geração.
        """)
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final SubmitReportUseCase submitReportUseCase;
    private final GetReportUseCase getReportUseCase;
    private final DownloadReportUseCase downloadReportUseCase;

    ReportController(
            SubmitReportUseCase submitReportUseCase,
            GetReportUseCase getReportUseCase,
            DownloadReportUseCase downloadReportUseCase
    ) {
        this.submitReportUseCase = submitReportUseCase;
        this.getReportUseCase = getReportUseCase;
        this.downloadReportUseCase = downloadReportUseCase;
    }

    @Operation(description = """
            Retorna todos os relatórios já pedidos, do mais recente para o mais antigo.
            Não possui corpo nem parâmetros.
            Diferente de `GET /reports/{id}`, esta lista não é filtrada por quem pediu: ela mostra \
            os relatórios de todos os usuários. O que sai aqui são apenas metadados — tipo, \
            período, estado e autor. O conteúdo continua restrito: baixar o arquivo de outra \
            pessoa responde `404`.
            O campo `downloadUrl` vem preenchido somente nos relatórios em `COMPLETED`.""")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ReportResponse>> listReports() {
        return ResponseEntity.ok(getReportUseCase.findAll());
    }

    @Operation(description = """
            Registra o pedido de um relatório e devolve imediatamente, sem esperar a geração. \
            O cabeçalho `Location` aponta para o endereço de acompanhamento.
            Campos obrigatórios: `type`, `format`, `startAt` e `endAt` (posterior a `startAt`). \
            Campo opcional: `roomId`, que restringe a apuração a uma sala — omitido, o relatório \
            cobre todas. Quem pede é o usuário autenticado, obtido do token.
            Responde `202`, e não `201`, porque o recurso pedido — o arquivo — ainda não existe \
            neste momento; o que existe é o acompanhamento.""")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Pedido registrado; a geração começa em seguida"),
            @ApiResponse(responseCode = "400", description = "Tipo ou formato desconhecido no corpo"),
            @ApiResponse(responseCode = "404", description = "Combinação de tipo e formato sem gerador disponível"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos, como intervalo sem fim posterior ao início")
    })
    @PostMapping
    public ResponseEntity<ReportResponse> submitReport(
            @AuthenticationPrincipal AuthUserDetails principal,
            @RequestBody SubmitReportRequest request) {
        var response = submitReportUseCase.execute(principal.getId(), request);
        return ResponseEntity.accepted()
                .location(URI.create("/reports/" + response.id()))
                .body(response);
    }

    @Operation(description = """
            Informa o estado do relatório. Responde `200` em qualquer estado — inclusive enquanto \
            está sendo gerado —, e é o campo `status` que diz se já dá para baixar. Um `404` aqui \
            significa que o id não existe ou não é seu, nunca que o relatório ainda não ficou pronto.
            Não possui corpo: o único dado obrigatório é o `id` na URL.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado atual do relatório"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReport(
            @AuthenticationPrincipal AuthUserDetails principal,
            @Parameter(description = "ID do relatório") @PathVariable UUID id) {
        return ResponseEntity.ok(getReportUseCase.execute(id, principal.getId()));
    }

    @Operation(description = """
            Entrega o arquivo gerado, como anexo. Só funciona em `COMPLETED`: pedir o arquivo de um \
            relatório ainda `PENDING` ou `PROCESSING`, que falhou ou que expirou responde \
            `422 INVALID_STATE`, o mesmo código que o restante da API usa para operação incompatível \
            com o estado do recurso.
            Não possui corpo: o único dado obrigatório é o `id` na URL.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arquivo do relatório"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "422", description = "Relatório ainda não concluído, falhado ou expirado")
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadReport(
            @AuthenticationPrincipal AuthUserDetails principal,
            @Parameter(description = "ID do relatório") @PathVariable UUID id) {
        ReportDownload download = downloadReportUseCase.execute(id, principal.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(download.filename(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .contentType(MediaType.parseMediaType(download.contentType()))
                .contentLength(download.sizeBytes())
                // InputStreamResource em vez dos bytes: o arquivo vai para a resposta em fluxo,
                // sem passar inteiro pela memória do servidor.
                .body(new InputStreamResource(download.content()));
    }
}
