# Concursos Platform

Plataforma de banco de questões para estudo de concursos (inspirada no Qconcursos).
Sprint 1 do roadmap: projeto Spring Boot rodando, entidades JPA e schema versionado com Flyway.

## Pré-requisitos

- JDK 17+
- Maven 3.9+ (ou use o `mvnw` se adicionar o wrapper com `mvn -N io.takari:maven:wrapper`)
- PostgreSQL rodando localmente

## Setup do banco

```bash
createdb concursos
```

As tabelas são criadas automaticamente pelo Flyway na primeira execução
(`src/main/resources/db/migration/V1__schema_inicial.sql`) — não precisa rodar SQL manual.

Ajuste usuário/senha em `src/main/resources/application.properties` se necessário.

## Rodando o projeto

```bash
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Estrutura atual

```
entity/       -> Disciplina, Banca, Questao, Alternativa, Usuario, Resposta
repository/   -> Interfaces JpaRepository (QuestaoRepository já preparado para
                 filtros combináveis via JpaSpecificationExecutor)
resources/db/migration/ -> Schema versionado (Flyway)
```

## Endpoint de listagem (Sprint 2)

```
GET /questoes?disciplinaId=1&bancaId=1&ano=2013&assunto=morfologia&page=0&size=20
```

Todos os parâmetros são opcionais e combináveis entre si (`QuestaoSpecification`
monta o filtro dinamicamente — só entra na query o que for informado).
Retorna paginado (`Page<QuestaoResponseDTO>`), e a listagem **não expõe qual
alternativa é a correta** — isso só aparece depois que o usuário responde
(endpoint de correção, Sprint 3).

Um seed com 3 disciplinas, 2 bancas e 1 questão de exemplo já roda automaticamente
(`V2__seed_dados.sql`), então dá pra testar direto:

```bash
curl "http://localhost:8080/questoes?assunto=morfologia"
```

## Próximos passos (Sprint 3 do roadmap)

1. Endpoint `POST /questoes/{id}/responder` recebendo o id da alternativa
   escolhida, gravando em `Resposta` e retornando certo/errado + `explicacao`.
2. Autenticação com Spring Security + JWT (o `Usuario` já existe; falta o
   fluxo de cadastro/login e o filtro de autenticação).
3. Depois disso, o dashboard do Sprint 4 já tem dado real pra agregar.

## Referência rápida do modelo de dados

Ver `src/main/resources/db/migration/V1__schema_inicial.sql` para o schema completo.
