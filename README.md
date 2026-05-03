# Kanflow Backend (Spring Boot)

API REST do Kanflow (Spring Boot + PostgreSQL + Flyway) com autenticação JWT e endpoints para workspaces, cards e histórico de sprints.

## Requisitos

- Java 17+ (recomendado 21)
- Maven
- PostgreSQL (local) **ou** Docker

## Rodar local (PostgreSQL local)

1. Ajuste as credenciais do banco:
   - Edite `src/main/resources/application.yml`, **ou**
   - Crie `config/application.yml` (use `config/application.example.yml` como base)
2. Defina o segredo JWT (PowerShell):

```powershell
set JWT_SECRET=dev-only-change-me-dev-only-change-me
```

3. Suba a API:

```powershell
mvn spring-boot:run
```

API em `http://localhost:9090`.

## Rodar com Docker Compose (Postgres + API)

```powershell
Copy-Item .env.example .env
docker compose up --build
```

## Swagger / OpenAPI

- Swagger UI: `http://localhost:9090/swagger-ui.html`
- OpenAPI JSON: `http://localhost:9090/api-docs`

## Autenticação (JWT)

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

Use o header:

```text
Authorization: Bearer <accessToken>
```

## Workspaces / Board

- `GET /api/workspaces`
- `POST /api/workspaces`
- `GET /api/workspaces/{id}`
- `PUT /api/workspaces/{id}`
- `DELETE /api/workspaces/{id}`
- `GET /api/workspaces/{id}/board`
- `PUT /api/workspaces/{id}/board`

## Cards (compatível com o front por status)

- `GET /api/cards?workspaceId=...`
- `POST /api/cards` (**workspaceId obrigatório**)
- `PATCH /api/cards/{id}`
- `POST /api/cards/{id}/move`
- `DELETE /api/cards/{id}`

## Sprints (histórico via snapshot)

- `POST /api/workspaces/{id}/sprints/complete` (salva snapshot e limpa cards do workspace)
- `POST /api/workspaces/{id}/board/blank` (limpa cards sem salvar histórico)
- `GET /api/workspaces/{id}/sprints`
- `GET /api/workspaces/{id}/sprints/{sprintHistoryId}`

## Busca

- `GET /api/workspaces/{id}/search?q=...`

# kanflow-backend