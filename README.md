# Smart Ticket Router

An AI-powered support ticket router built with Spring Boot. It reads a
plain-English support message, asks OpenAI to classify it, saves it,
and stores a semantic embedding of it in ChromaDB for similarity
search — all behind a full login/role system with an admin dashboard.

## Overview

Support teams triage incoming requests by hand: reading the message,
deciding what category it belongs to, how urgent it is, and which team
should own it. Smart Ticket Router automates that first step. A user
submits a ticket through the web form (or the REST API); the
application asks an OpenAI model to return a structured classification
— category, priority, assigned team, and a one-line reason — and
persists the ticket, linked to whoever submitted it.

## Features

- AI-powered ticket classification (category, priority, assigned team, reasoning)
- Structured JSON output, enforced via prompt design and Jackson deserialization
- Graceful handling of AI unreliability: if OpenAI returns unparsable JSON,
  the request is retried once, then falls back to a safe default
  classification instead of failing outright
- User registration and login (Spring Security, BCrypt-hashed passwords)
- Role/permission-based authorization (`ROLE_USER` / `ROLE_ADMIN`, with
  fine-grained permissions such as `CREATE_TICKET`, `VIEW_HISTORY`,
  `VIEW_ALL_TICKETS`, `ASSIGN_TEAM`, `DELETE_TICKET`)
- Personal ticket history page for each user
- Admin dashboard with a filterable, sortable view of every ticket
  (filter by priority and/or workflow status; tickets are ranked by
  severity by default) and inline ticket status updates
  (`OPEN` → `IN_PROGRESS` → `RESOLVED` → `CLOSED`)
- Semantic/similarity search over past tickets via ChromaDB embeddings
- Centralized logging and exception handling via Spring AOP
  (method entry/exit/timing, exception logging, and transaction
  commit/rollback logging)
- JUnit 5 + Mockito unit test suite

## Technology Stack

- Java 21
- Spring Boot 4.1 (Web MVC, Security, Data JPA, AOP, Validation, Thymeleaf)
- Maven
- PostgreSQL
- Thymeleaf + Bootstrap 5
- OpenAI API (chat completions + embeddings)
- ChromaDB (vector database, optional at runtime — see below)
- Jackson
- JUnit 5 / Mockito (test)

## Project Structure

```
src/main/java/com/example/smart_ticket_router/
├── SmartTicketRouterApplication.java   application entry point
├── aspect/         cross-cutting concerns: logging, exception logging, transaction/rollback logging
├── client/         OpenAIClient, ChromaClient — external HTTP integrations
├── config/         SecurityConfig, TransactionConfig, ChromaConfig, AdminInitializer
├── controller/     web (Thymeleaf) and REST controllers
├── entity/         JPA entities: User, Role, Permission, Ticket
├── enums/          Priority, TicketCategory, AssignedTeam, TicketStatus
├── exception/      custom exceptions + GlobalExceptionHandler
├── initializer/    startup seeding: roles/permissions, legacy user role backfill
├── model/          request/response DTOs
├── prompt/         PromptBuilder — constructs the OpenAI prompt
├── repository/     Spring Data JPA repositories
└── service/        business logic: TicketRoutingService, UserService, EmbeddingService, ...

src/main/resources/
├── application.properties.example      copy to application.properties and fill in
└── templates/                          Thymeleaf views (index, login, register, history, admin-*)

src/test/java/...                       JUnit 5 + Mockito unit tests
```

## Prerequisites

- Java 21+
- Maven (or use the bundled `./mvnw`)
- PostgreSQL running locally, with a database created (default name
  `smart_ticket_router`)
- An OpenAI API key
- ChromaDB running locally (optional — see [ChromaDB is optional](#chromadb-is-optional))

## Running the Project

1. Copy the example config and fill in your real values:

   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

   Edit `application.properties` and set:
   - `spring.datasource.username` / `spring.datasource.password` — your local PostgreSQL credentials
   - `openai.api.key` — your OpenAI API key

   This file is gitignored — your credentials never get committed.

2. Make sure PostgreSQL is running and the `smart_ticket_router` database exists.

3. (Optional) Start ChromaDB locally on `http://localhost:8000` if you want embeddings/semantic search to work.

4. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

5. Open [http://localhost:8080](http://localhost:8080).

6. On first startup, the application automatically seeds the roles/permissions
   and creates a default administrator account:

   - **Admin login:** `admin@example.com` / `admin123`
   - Regular users register via the **Register** link on the login page.

### ChromaDB is optional

Ticket classification, persistence, and the full web/admin experience
all work without ChromaDB running. If ChromaDB is unreachable at
startup or at request time, the application logs a warning and
continues — only embedding storage and semantic search are affected.

## Using the App

- **Anyone (after registering):** submit a ticket from the home page,
  see the AI's classification, and view your own ticket history under
  **My Tickets**.
- **Admin:** open the **Dashboard** to see quick links, or **All
  Tickets** to view every ticket, filter by priority and/or status, and
  update a ticket's status inline.

## REST API

**POST** `/api/route` — classify and persist a ticket (requires the `CREATE_TICKET` authority)

```json
{
  "message": "I forgot my password."
}
```

Response:

```json
{
  "category": "AUTHENTICATION",
  "priority": "MEDIUM",
  "assignedTeam": "ACCOUNT_SUPPORT",
  "reason": "Password reset assistance required."
}
```

**GET** `/api/tickets/{id}` — fetch a single ticket by ID (requires the `VIEW_ALL_TICKETS` authority)

## Architecture

```
User
  │
  ▼
Web form (Thymeleaf) or POST /api/route
  │
  ▼
TicketWebController / TicketApiController
  │
  ▼
TicketRoutingService  ──►  PromptBuilder  ──►  OpenAIClient  ──►  OpenAI
  │                          (on unparsable JSON: retry once, then
  │                           fall back to a default classification)
  ▼
TicketRepository (PostgreSQL)
  │
  ▼
EmbeddingService ──► ChromaClient ──► ChromaDB   (best-effort; failures are logged, not fatal)
  │
  ▼
JSON / HTML response back to the user
```

Cross-cutting concerns (method logging, exception logging, and
transaction commit/rollback logging) are applied declaratively via
Spring AOP aspects rather than scattered through the business logic —
see `aspect/`.

## Handling AI Unreliability

Large language models occasionally return malformed JSON or use a
value outside the fixed set of categories/priorities/teams this
application understands. `TicketRoutingService.classifyTicket` treats
this as an expected failure mode:

1. Parse the first OpenAI response.
2. If parsing fails, retry the exact same prompt once.
3. If the retry also fails to parse, fall back to a safe default
   classification (`GENERAL_SUPPORT` / `MEDIUM` / `CUSTOMER_SUPPORT`)
   so the ticket is still saved and routed for manual review, instead
   of the request failing with an error the end user can't act on.

A genuine failure to reach OpenAI at all (invalid API key, network
outage) is handled differently — it is not retried or masked; it is
surfaced as a clear `502 Bad Gateway` error.

## Sample Tickets & Edge Cases

`sample-tickets.md` at the repository root contains 20 sample tickets
(including an angry-tone ticket and a vague one-word ticket) used to
demo the router end-to-end. `PromptBuilder` explicitly instructs the
model on the three required edge cases: an angry customer should raise
the priority, a very short message should still be classified by
inferring the most likely category, and a genuinely ambiguous ticket
should default to `GENERAL_SUPPORT`.

## Manual vs. AI Routing Time

_Fill this in with your own measured numbers before your demo — this
is intentionally left as a template rather than fabricated data._

| Ticket | Manual routing time (a person reads it and decides category/priority/team) | AI routing time (`POST /api/route` round-trip) |
|--------|:---:|:---:|
| Example: "I forgot my password." | ~30–60s | ~X.Xs |
| ... | | |

A simple way to produce this: time yourself manually classifying 5–10
tickets from `sample-tickets.md`, then time how long the same tickets
take via the API (check the `Ticket routing completed successfully`
timing already logged by `LoggingAspect`, or wrap the API call with
`time curl ...`).

## Running the Tests

```bash
./mvnw test
```

The test suite (`src/test/java/...`) covers user registration, the
ticket-routing orchestration logic (including OpenAI failure/retry/
fallback and ChromaDB failure tolerance), and the admin
filtering/status-update logic — all as fast, dependency-free unit
tests using JUnit 5 and Mockito.
