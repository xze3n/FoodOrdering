# Food Ordering System — Microservices

Distributed backend for a simplified food ordering platform built with Java, Spring Boot, Docker, and Kubernetes.

---

## Team Assignments

| Student | Responsibility |
|---------|---------------|
| 1 | **User Service** + Spring Security (JWT) |
| 2 | **Menu Service** |
| 3 | **Order Service** |
| 4 | **API Gateway** + Docker Compose + Kubernetes |

> **Student 4** owns `docker-compose.yml` and all files under `k8s/`. The other three must provide a working `Dockerfile` in their service folder.

---

## Repository Structure

```
FoodOrdering/
├── pom.xml                   ← parent POM (do not edit unless agreed)
├── .gitignore
├── docker-compose.yml        ← owned by Student 4
├── README.md
│
├── user-service/             ← Student 1
├── menu-service/             ← Student 2
├── order-service/            ← Student 3
└── api-gateway/              ← Student 4
    k8s/
    ├── user-service/         ← deployment.yml + service.yml (Student 4)
    ├── menu-service/
    ├── order-service/
    └── api-gateway/
```

---

## Service Ports

| Service | Local Port | Swagger UI |
|---------|-----------|------------|
| api-gateway | `8080` | http://localhost:8080/swagger-ui/index.html |
| user-service | `8081` | http://localhost:8081/swagger-ui/index.html |
| menu-service | `8082` | http://localhost:8082/swagger-ui/index.html |
| order-service | `8083` | http://localhost:8083/swagger-ui/index.html |

---

## Databases (PostgreSQL)

Each service uses its own isolated PostgreSQL database. Student 4 decides the host ports in `docker-compose.yml`.

| Service | Database name | Default local port (suggestion) |
|---------|--------------|--------------------------------|
| user-service | `userdb` | `5433` |
| menu-service | `menudb` | `5434` |
| order-service | `orderdb` | `5435` |
| api-gateway | — | no database |

---

## Environment Variables

Each service reads configuration from environment variables. Defaults are set in `application.yml` for local dev.

### user-service, menu-service, order-service

| Variable | Default (dev) | Description |
|----------|--------------|-------------|
| `DB_HOST` | `localhost` | PostgreSQL hostname |
| `DB_USERNAME` | `postgres` | DB user |
| `DB_PASSWORD` | `postgres` | DB password |
| `JWT_SECRET` | *(set in application.yml)* | Must be ≥32 chars |

### order-service (additional)

| Variable | Default (dev) | Description |
|----------|--------------|-------------|
| `MENU_SERVICE_HOST` | `localhost` | Hostname used to call Menu Service |
| `MENU_SERVICE_PORT` | `8082` | Port of Menu Service |

### api-gateway (additional)

| Variable | Default (dev) | Description |
|----------|--------------|-------------|
| `USER_SERVICE_HOST` | `localhost` | |
| `MENU_SERVICE_HOST` | `localhost` | |
| `ORDER_SERVICE_HOST` | `localhost` | |

---

## Inter-Service Communication

```
Client → API Gateway (8080)
              ├─► /users/**    → user-service  (8081)
              ├─► /menu/**     → menu-service  (8082)
              └─► /orders/**   → order-service (8083)

order-service ──REST──► menu-service  (validate + fetch menu items)
```

Communication uses plain REST/JSON. Order Service calls Menu Service to validate items before creating an order.

---

## Security

- JWT tokens are issued by **user-service** on login.
- **menu-service** and **order-service** validate the token on every request.
- The `JWT_SECRET` value **must be identical** across all three services.
- Roles: `CUSTOMER`, `ADMIN`.

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- PostgreSQL (or Docker for containerised databases)

### 1. Clone the repository

```bash
git clone <repo-url>
cd FoodOrdering
```

### 2. Validate the Maven multi-module build

```bash
mvn validate
```

All 4 modules (`user-service`, `menu-service`, `order-service`, `api-gateway`) should resolve without errors.

### 3. Run your service locally

Create a local PostgreSQL database matching your service's DB name, then run:

```bash
# example for user-service
cd user-service
mvn spring-boot:run
```

Or set env vars explicitly:

```bash
DB_HOST=localhost DB_USERNAME=postgres DB_PASSWORD=postgres mvn spring-boot:run
```

### 4. Build the project

```bash
mvn clean package -DskipTests
```

---

## Docker & Kubernetes

`docker-compose.yml` and all files under `k8s/` are **owned by Student 4** and are currently empty.  
Each other student must provide a working `Dockerfile` in their service root before Student 4 can wire everything together.

### Dockerfile expectations (Students 1–3)

- Multi-stage build: Maven build → JRE runtime.
- Final image exposes the correct port (see table above).
- Reads configuration from environment variables (no hardcoded secrets).

---

## Git Workflow

| Branch | Purpose |
|--------|---------|
| `main` | Stable, deployable code |
| `develop` | Integration branch — merge feature branches here |
| `feature/<name>` | One branch per feature/task |

**Workflow:**
1. Branch off `develop`: `git checkout -b feature/my-feature develop`
2. Open a Pull Request targeting `develop`.
3. At least one teammate must review before merging.
4. `main` is updated only at the end of each week with a stable snapshot.

---

## Order Status Flow

```
CREATED → CONFIRMED → COMPLETED
               ↓
           CANCELLED
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (jjwt 0.12) |
| Persistence | Spring Data JPA + PostgreSQL |
| Gateway | Spring Cloud Gateway (reactive) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5 + Mockito |
| Containerisation | Docker |
| Orchestration | Kubernetes |
| Build | Maven (multi-module) |
