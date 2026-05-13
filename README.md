# Kanflow Backend

Backend REST do **Kanflow**: aplicação de gestão tipo Kanban com **workspaces**, **colunas de board**, **cards** (pontos de story, tempo estimado, responsável, status), **checklist**, **comentários**, **histórico de sprints** (snapshots ao concluir sprint) e **billing** (planos Free / Basic / Full com checkout mock em desenvolvimento).

Este repositório é a **API Spring Boot** consumida pelo frontend (ou por ferramentas como Swagger / Postman).

---

## Stack técnica

| Camada | Tecnologia |
|--------|------------|
| Runtime | Java **17** (definido no `pom.xml`; Docker usa imagem Temurin 21) |
| Framework | **Spring Boot 3.4** (Web, Data JPA, Security, Validation) |
| Base de dados (produção / dev real) | **PostgreSQL** |
| Base de dados (dev rápido) | **H2** em memória (perfil `local`) |
| Migrações | **Flyway** (ativas com PostgreSQL; desligadas no perfil `local`) |
| Auth | **JWT** (Bearer) + BCrypt para passwords |
| Documentação API | **springdoc-openapi** (Swagger UI) |

---

## Bases de dados que o projeto usa

O projeto suporta **duas formas** de ligar à base. Escolhe **uma** por arranque; não mistures expectativas (por exemplo Flyway + H2 com `ddl-auto`).

### 1) PostgreSQL (recomendado para desenvolvimento contínuo e igual à produção)

- **URL por defeito** (em `src/main/resources/application.yml`): `jdbc:postgresql://localhost:5432/kanflow`
- **Utilizador / password por defeito no YAML**: `postgres` / `12345678`
- **Flyway**: **ligado** — ao subir a app, as migrações em `src/main/resources/db/migration` aplicam-se automaticamente.
- **JPA**: `hibernate.ddl-auto: validate` (o schema vem do Flyway, não do Hibernate).

**Criar a base no PostgreSQL** (uma vez), por exemplo com `psql` ou pgAdmin:

```sql
CREATE DATABASE kanflow;
```

Garante que o utilizador que defines em `application.yml` (ou em `config/application.yml`) tem permissões sobre a base `kanflow`.

**Sobrescrever ligação sem alterar ficheiros versionados**

1. Cria a pasta `config/` na raiz do repositório (se ainda não existir).
2. Copia o exemplo:

```powershell
Copy-Item config\application.example.yml config\application.yml
```

3. Edita `config/application.yml` com a tua URL, utilizador e password reais.

O Spring Boot carrega `config/application.yml` com **prioridade** sobre `src/main/resources/application.yml`.

### 2) H2 em memória (perfil `local`)

- Ativado com: `-Dspring-boot.run.profiles=local` (ou `SPRING_PROFILES_ACTIVE=local`).
- **Flyway**: **desligado**; o Hibernate usa `ddl-auto: create-drop` (schema criado em runtime; **dados perdem-se** ao encerrar a JVM).
- Útil para testar a API sem instalar PostgreSQL.

---

## Variáveis de ambiente úteis

| Variável | Efeito | Exemplo |
|----------|--------|---------|
| `JWT_SECRET` | Chave de assinatura do JWT (mínimo recomendado: segredo forte; o código faz padding se for curto) | `set JWT_SECRET=...` (CMD) ou `$env:JWT_SECRET="..."` (PowerShell) |
| `SERVER_PORT` | Porta HTTP do servidor | `9090` (padrão no YAML) |
| `SPRING_DATASOURCE_URL` | URL JDBC (sobrescreve YAML) | usado pelo Docker Compose na API |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | Credenciais JDBC | idem |
| `KANFLOW_BILLING_MOCK` | Confirmação simulada de pagamento (`true`/`false`) | `true` em dev |
| `KANFLOW_SEED_DEMO_RUN` | Liga o seed automático só se também usares o perfil `seed-demo` | ver secção Seed |

---

## Como correr o projeto

### Pré-requisitos

- **Java 17+** e **Maven** instalados (`java -version`, `mvn -version`).
- Para o fluxo PostgreSQL: **PostgreSQL** acessível ou **Docker**.

### A) Maven + PostgreSQL local

1. Cria a base `kanflow` e alinha utilizador/password com `application.yml` ou com `config/application.yml`.
2. Define o segredo JWT (PowerShell):

```powershell
$env:JWT_SECRET = "dev-only-change-me-dev-only-change-me"
```

3. Na raiz do repositório:

```powershell
mvn spring-boot:run
```

4. API (por defeito): `http://localhost:9090`

### B) Maven + H2 (sem PostgreSQL)

```powershell
$env:JWT_SECRET = "dev-only-change-me-dev-only-change-me"
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

### C) Docker Compose (PostgreSQL + API no mesmo compose)

1. Copia variáveis de ambiente:

```powershell
Copy-Item .env.example .env
```

2. Sobe os serviços:

```powershell
docker compose up --build
```

O compose define por defeito:

- Utilizador da base: **`kanflow`**
- Password: **`12345678`**
- Base: **`kanflow`**

A API no container recebe `SPRING_DATASOURCE_*` apontando para o serviço `postgres`.

**Importante:** o ficheiro `src/main/resources/application.yml` (Maven local **sem** Docker) usa o utilizador **`postgres`**. O Docker Compose usa **`kanflow`**. Se correres **só** o Postgres do compose e a API **fora** do Docker com o YAML por defeito, a autenticação pode falhar até alinhares credenciais em `config/application.yml` ou criares o role `postgres` na mesma base — o caminho mais simples é usar **`config/application.yml`** com `username: kanflow` e a password do `.env`.

O ficheiro **`.env`** é lido pelo **Docker Compose**, não pelo `mvn spring-boot:run` local (a menos que exportes as mesmas variáveis no terminal).

### Porta já em uso

Se aparecer `Port 9090 was already in use`:

```powershell
$env:SERVER_PORT = "9091"
mvn spring-boot:run
```

Ou encerra o processo que está à escuta na 9090.

---

## Seed de dados para demo / performance

Existe um perfil **`seed-demo`** que cria utilizador de demo, workspace, histórico de sprints e cards no quadro (ver código em `com.kanflow.seed`).

**Com H2 (rápido):**

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\run-seed-demo.ps1
```

Ou manualmente:

```powershell
$env:JWT_SECRET = "dev-only-change-me-dev-only-change-me"
mvn spring-boot:run "-Dspring-boot.run.profiles=local,seed-demo"
```

No log, no fim do arranque, aparecem email, password e `workspaceId` do utilizador demo.

Se a política de execução do PowerShell bloquear `.ps1`, usa o comando com `-ExecutionPolicy Bypass` acima.

---

## Swagger / OpenAPI

| Recurso | URL (porta por defeito 9090) |
|---------|------------------------------|
| Swagger UI | `http://localhost:9090/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:9090/api-docs` |

---

## Autenticação (JWT)

| Método | Rota |
|--------|------|
| Registo | `POST /api/auth/register` |
| Login | `POST /api/auth/login` |
| Utilizador atual | `GET /api/auth/me` |

Em rotas protegidas, envia:

```http
Authorization: Bearer <accessToken>
```

---

## Recursos principais da API (resumo)

**Workspaces e board**

- `GET/POST /api/workspaces`
- `GET/PUT/DELETE /api/workspaces/{id}`
- `GET/PUT /api/workspaces/{id}/board`

**Cards**

- `GET /api/cards?workspaceId=...`
- `POST /api/cards` (body com `workspaceId`)
- `PATCH /api/cards/{id}`
- `POST /api/cards/{id}/move`
- `DELETE /api/cards/{id}`

**Sprints / histórico**

- `POST /api/workspaces/{id}/sprints/complete` — grava histórico + snapshot e **remove os cards** do workspace
- `POST /api/workspaces/{id}/board/blank` — limpa cards sem gravar sprint
- `GET /api/workspaces/{id}/sprints`
- `GET /api/workspaces/{id}/sprints/{sprintHistoryId}`

**Busca**

- `GET /api/workspaces/{id}/search?q=...`

**Billing (dev)**

- Planos e limites no serviço de billing; checkout mock e confirmação simulada conforme `kanflow.billing.mock-confirmation`.

Consulta o Swagger para o corpo exato dos JSON e códigos de resposta.

---

## Estrutura do repositório (visão geral)

```
src/main/java/com/kanflow/     Código da aplicação (api, service, domain, repository, config, auth, billing, seed)
src/main/resources/
  application.yml              Configuração por defeito (PostgreSQL)
  application-local.yml        Perfil H2
  application-seed-demo.yml    Perfil seed (liga kanflow.seed-demo.run)
  db/migration/                Scripts Flyway (versão da base)
config/application.example.yml  Exemplo para overrides locais (gitignore em application.yml real)
scripts/run-seed-demo.ps1      Arranque local + seed-demo
docker-compose.yml             Postgres + API
.env.example                   Variáveis para Docker Compose
pom.xml                        Dependências e build Maven
```

---

## Testes e build

```powershell
mvn test
mvn package -DskipTests
```

---

## Resolução de problemas

| Problema | O que verificar |
|----------|-----------------|
| `28P01` (autenticação PostgreSQL falhou) | Utilizador/password na URL JDBC; preferir `config/application.yml` |
| `Connection refused` em `localhost:5432` | PostgreSQL a correr; ou usar perfil `local` (H2) |
| Porta em uso | `SERVER_PORT` ou libertar a porta |
| Flyway falha ao migrar | Versão da base vs scripts em `db/migration`; não uses `local` se queres Flyway nesta instância |
| Swagger não bate com o host | Abre o Swagger no **mesmo host/porta** da API; o OpenAPI lista servidores local |

---

## Licença e contexto académico

Projeto de backend associado ao produto **Kanflow** (gestão de trabalho em estilo Kanban). Ajusta licença e autores conforme a regra da tua instituição, se aplicável.
