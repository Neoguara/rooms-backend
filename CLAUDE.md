# rooms-backend

API de reserva de salas (Spring Boot 4 + Spring Modulith, Java 25, H2 em arquivo).

Um evento **não é criado direto**: toda escrita passa por um `EventRequest`, que agrupa alterações
e é aprovado ou rejeitado por inteiro. Entender esse fluxo é pré-requisito para mexer no módulo
`event`.

## Build e testes

**Não existe Maven utilizável neste ambiente**: `./mvnw` está quebrado (falta `.mvn/wrapper/`) e não
há `mvn` no PATH. Não sugira comandos `mvn` — eles vão falhar.

Para compilar e rodar testes de domínio, monte o classpath a partir do `~/.m2`, que já está
populado:

```sh
CP=$(find ~/.m2/repository -name "*.jar" ! -name "*sources*" ! -name "*javadoc*" | tr '\n' ':')
javac -nowarn -d out      -cp "$CP"       $(find src/main/java -name "*.java")
javac -nowarn -d test-out -cp "out:$CP"   $(find src/test/java -name "*.java")
```

Não há `junit-platform-console-standalone` no `~/.m2`, mas `junit-platform-launcher` está lá: uma
classe `main` curta com `LauncherFactory` + `SummaryGeneratingListener` roda uma classe de teste por
vez.

Isso vale só para **teste de domínio puro**. `RoomsApplicationTests` é `@SpringBootTest`, sobe o
contexto contra o H2 em arquivo com `ddl-auto=update`, e precisa ser executado pelo IntelliJ — rodar
fora dele disputa o lock e altera o banco de desenvolvimento.

O app roda pelo IntelliJ, que mantém lock exclusivo sobre `data/rooms.mv.db`. Qualquer acesso
externo ao banco exige encerrar o processo `com.neoguara.rooms.RoomsApplication` antes.

## Arquitetura

Módulos Spring Modulith em `com.neoguara.rooms`: `auth`, `user`, `room`, `event`, `shared`.

Cada módulo de negócio segue as mesmas três camadas:

```
domain/         entities, valueobjects, enums, validation, services, exceptions
application/    ports (interfaces), usecases, dtos, mappers
infrastructure/ web (controllers), repositories, adapters, config
```

Dependências apontam para dentro: `infrastructure` → `application` → `domain`. O domínio não importa
Spring nem JPA-específicos além das anotações de mapeamento.

Comunicação entre módulos é por porta: o módulo consumidor declara a interface em
`application/ports`, o módulo provedor implementa em `infrastructure/adapters`. Exemplo:
`room.application.ports.RoomAvailabilityPort` ← `event.infrastructure.adapters.EventAvailabilityAdapter`.

## Convenções de domínio

Estas são deliberadas. Skills genéricas de Clean Code / SOLID / Spring podem sugerir o contrário —
**prefira o que está aqui** e, se discordar, levante a questão em vez de reescrever em silêncio.

**Entidades se validam sozinhas.** Construtor sem argumentos é package-private (exigência do JPA), o
construtor completo é privado, e a criação passa por um factory estático `create(...)` que roda o
`Validator<T>` do domínio acumulando erros num `Notification` e chama `raiseIfHasErrors()`. Não crie
entidade por construtor público nem valide no use case o que a entidade já valida.

**Transições de estado são métodos com guarda**, não setters: `cancel()`, `reactivate()`,
`complete()`, `archive()`, `discard()`. Estado inválido lança `InvalidStateException` de dentro da
entidade. Não existe setter público em entidade.

**Evento não se apaga.** Não existe delete de evento, nem hard nem soft — `CANCEL` cumpre esse
papel: é decisão legítima, reversível, e libera a sala. `DISCARD` não é solicitável: só surge ao
reverter um `CREATE` aprovado por engano. Não crie endpoint `DELETE /events/{id}`; ele furaria a
regra de que toda escrita passa por aprovação.

`Event.complete()` e `Event.archive()` **não têm chamador** — `COMPLETED` e `ARCHIVED` são
inalcançáveis, e ficaram assim por decisão. Por isso `EventStatus.occupiesRoom()` cita um estado que
nunca ocorre.

**Value objects são records** com guarda de nulo no construtor compacto e um `of(...)` estático.

**Erros** descendem de `BusinessException` (`ConflictException`, `InvalidStateException`,
`ResourceNotFoundException`, `DomainValidationException`) e são traduzidos em HTTP por
`shared.infrastructure.web.GlobalExceptionHandler`, sempre no formato `ErrorResponse`. Não trate
exceção de negócio dentro de controller.

**Mappers** são classes utilitárias com construtor privado e métodos estáticos. **Use cases** são
`@Service` com um método `execute(...)` público.

## Invariante que não pode ser contornada

Nenhum evento ocupa uma sala já ocupada no mesmo intervalo. Isso é garantido **em tempo de
compilação**, por double dispatch: `Event.create`, `Event.update` e `Event.reactivate` exigem um
`RoomOccupancy` na assinatura e recusam o horário por conta própria. `cancel`/`discard`/`archive`
não recebem nada, porque liberam a sala.

Consequência prática: um caso de uso novo que escreva evento **não compila** sem fornecer a agenda.
Não contorne isso passando uma implementação vazia de `RoomOccupancy` — se a checagem atrapalha,
o problema é o desenho, não a checagem.

Quem decide o que ocupa sala é `EventStatus.occupiesRoom()` (hoje `ACTIVE` e `COMPLETED`) — fonte
única, consumida tanto pela busca de salas disponíveis quanto pela checagem de conflito. Não
reintroduza status hardcoded em query ou adapter.

Intervalos são **semiabertos**: eventos colados (`fim == início`) não conflitam.

## Recorrência

Não existe entidade de série. Uma recorrência é **um evento por ocorrência**, todos amarrados pelo
mesmo `SeriesId` — cada ocorrência já tem sala, horário e regra próprios, então um agregado à parte
só duplicaria dados. "A série" é `WHERE series_id = ?`.

A expansão acontece na **submissão** (`RequestEventChangesUseCase`), não na aprovação. É isso que
mantém `EventChangeItem`, `ReviewEventRequestUseCase` e `ReverseEventRequestUseCase` sem saber que
recorrência existe: eles continuam vendo um item por evento. Quem aprova vê as N datas antes de
decidir, e a checagem de conflito roda por ocorrência, de graça. **Não mova a expansão para a
aprovação** — isso quebraria as três coisas de uma vez.

`RecurrenceRule` cobre um subconjunto do RFC 5545 (`FREQ`, `INTERVAL`, `BYDAY`, `COUNT`/`UNTIL`) e
**exige** limite, ao contrário do RFC: sem `COUNT` ou `UNTIL` não há o que materializar. Teto de 200
ocorrências, e série maior é recusada em vez de truncada.

`RecurrenceExpander` anda só em unidades de calendário (`plusDays`/`plusWeeks`/`plusMonths`). Somar
7×24h atravessa a virada do horário de verão errado e desloca o horário local da série inteira —
tem teste para isso.

`ChangeScope` (`THIS_OCCURRENCE`, `THIS_AND_FOLLOWING`, `ALL_OCCURRENCES`) vale para `CANCEL`,
`REACTIVATE` e `UPDATE`. Em lote, ocorrências fora do estado exigido **ou já vencidas** são
descartadas, não viram erro: como o grupo vale todo ou nada, um cancelamento avulso anterior — ou
uma ocorrência antiga — derrubaria a operação sobre o resto da série.

**`UPDATE` em lote desloca, não reposiciona.** `startAt`/`endAt` valem como diferença em relação ao
evento em `eventId`: cada ocorrência recebe `data + N dias`, no horário e duração novos. É assim que
uma série de terça inteira vai para quarta. Aplicar a data absoluta a todas empilharia a série no
mesmo instante. O deslocamento é `OccurrenceShift`, que guarda **dias de calendário + horário**, não
`Duration` — somar 24h quebra no horário de verão, mesma armadilha do `RecurrenceExpander`.

**`recurrenceRule` não é editável por `UPDATE`.** Mudar o padrão é encerrar a série e abrir outra
(`CANCEL` das restantes + `CREATE` da nova, no mesmo grupo). Quem mexe nela é o sistema: mover a
série **inteira** reescreve o `BYDAY` com os dias resultantes. Movendo só parte, a regra fica como
está — nenhuma RRULE única descreveria metade numa terça e metade numa quarta, e gravar um `BYDAY`
que contradiz parte das ocorrências é pior do que manter o padrão de origem.

## Testes

JUnit 5 com asserções puras (`assertEquals`, `assertThrows`, `assertDoesNotThrow`). AssertJ vem
transitivamente pelos starters de teste, mas **não é o estilo do projeto** — não converta os testes
existentes para ele.

Testes de domínio não sobem Spring. Portas são interfaces, então preferimos lambdas e fakes a
mocks; Mockito só onde a porta tem muitos métodos (ver `GetEventRequestUseCaseTest`).

Nome de teste descreve a regra, não o método: `backToBackEventsDoNotCompete`, não `testOverlap`.

## Idioma

Javadoc e comentários em **português**, explicando *por que* — não *o que*. Identificadores,
nomes de teste e mensagens de erro de runtime em **inglês** (`"Only active events can be updated"`).
Documentação de API (`@Schema`, `@Operation` do springdoc) em português, porque é o que o consumidor
da API lê.

## Armadilhas conhecidas

- **Enum de status vira `ENUM(...)` nativo do H2, não `varchar`.** Todos os `status` já estão em
  `@Enumerated(EnumType.STRING)` — a armadilha do ordinal foi migrada. Mas a coluna gerada lista os
  valores permitidos, então **acrescentar constante a um enum de status exige alterar a coluna**;
  `ddl-auto=update` não amplia o `ENUM` sozinho. Reordenar, por outro lado, deixou de ser problema.
- **Sem controle de concorrência.** Duas aprovações simultâneas passam as duas na checagem de
  conflito e gravam. Só fecha com constraint no banco — no Postgres, `EXCLUDE USING gist (room_id
  WITH =, tsrange(start_at, end_at) WITH &&)`. H2 não suporta.
- **`ddl-auto=update`**: o schema deriva das entidades e não há migrations. Remover ou renomear
  campo não limpa a coluna antiga, e mudança de tipo pode falhar em silêncio.
- **Aprovar uma série grande dispara uma consulta de conflito por ocorrência.** Com o teto de 200
  é lento, não quebrado. Não dá para trocar por uma `LoadedOccupancy` carregada uma vez: cada
  ocorrência precisa enxergar as anteriores da mesma transação.

## Skills instaladas

`.claude/skills/` traz 15 skills de Java/Spring vindas de `github.com/decebals/claude-code-java`.
Foram removidas as incompatíveis: `maven-dependency-audit` (todo o workflow é `mvn`),
`java-migration` (o projeto já está no topo da faixa que ela cobre) e `changelog-generator` (o
repositório não tem tags nem histórico de commits convencionais).

As skills são genéricas e não conhecem as convenções acima. Leia-as **dentro** deste contexto: onde
divergirem, este arquivo vence.
