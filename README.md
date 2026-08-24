# VaultGuard

VaultGuard is a full-stack fraud detection platform that screens financial transactions in real time, applies JWT-based security, rate-limits requests with Redis, streams events through Kafka, and stores results in PostgreSQL.

## What it does

- Authenticates users with JWT cookies
- Accepts transaction requests and evaluates fraud risk
- Applies IP-based and user-based rate limiting
- Persists transactions and risk results to PostgreSQL
- Seeds demo data in the `dev` profile
- Provides a React/Vite dashboard for interacting with the backend

## Architecture

```text
Client / Frontend
        ↓
CORS + JWT Auth + Redis Rate Limiter
        ↓
REST Controllers
        ↓
Service Layer
        ↓
Kafka Producer / Consumer
        ↓
Fraud Rules + Risk Scoring
        ↓
PostgreSQL + WebSocket Updates
```

## Tech Stack

### Backend
- Java 21
- Spring Boot 3.5.x
- Spring Security
- Spring Data JPA
- Spring Kafka
- Spring WebSocket
- Redis
- PostgreSQL
- Bucket4j
- Swagger / OpenAPI

### Frontend
- React 19
- Vite
- TypeScript
- Axios
- React Router

## Project Structure

```text
VaultGuard/
├── backend/          # Spring Boot API, Docker setup, scripts, env files
├── frontend/         # React/Vite app
├── README.md         # Project guide
└── payload.json      # Sample request payload
```

## Prerequisites

- Java 21 JDK
- Maven 3.9+
- Node.js 18+ or 20+
- Docker Desktop

## Environment Files

### Backend

Copy the example files before running:

```bash
copy backend\.env.example backend\.env
copy backend\.env.docker.example backend\.env.docker
```

**Important:** Change the copied values before you build or start the app. Leaving placeholder values in place can cause Docker startup or the backend build/run to fail.

### Frontend

Create `frontend/.env` from `frontend/.env.example` and set:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

## Quick Start

### Option 1: Docker stack + backend locally

1. Copy and edit the backend env files.
2. Start infrastructure and the app with Docker Compose:

```bash
cd backend
docker compose up -d
```

3. Run the backend without seed data:

```bash
mvn spring-boot:run
```

4. Or run with demo seed data:

```bash
mvn clean spring-boot:run "-Dspring-boot.run.profiles=dev"
```

### Option 2: Frontend locally

```bash
cd frontend
npm install
npm run dev
```

## Backend Run Modes

### Production-style run

```bash
cd backend
mvn spring-boot:run
```

### Dev profile with seed data

```bash
cd backend
mvn clean spring-boot:run "-Dspring-boot.run.profiles=dev"
```

The `dev` profile seeds sample users, banks, transactions, and risk-management rows.

## Docker Setup

`backend/docker-compose.yml` starts:

- Kafka
- Redis
- PostgreSQL
- the Spring Boot application

The application container uses `backend/.env.docker` through `env_file`.

## Frontend Scripts

From `frontend/`:

```bash
npm run dev
npm run build
npm run lint
npm run preview
```

## Backend Scripts

From `backend/scripts/`:

```bash
./run-script.sh
./auth-test.sh
```

- `run-script.sh` tests authenticated transaction submission and rate limiting
- `auth-test.sh` stress tests login/register flows

## Main API Endpoints

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`

### Transactions / Fraud

- `POST /api/v1/fraudDetect/processTransaction`
- `GET /api/v1/fraudDetect/fetch/allTransactions`
- `GET /api/v1/fraudDetect/fetch/transactionById/{id}`

### Banks

- `GET /api/v1/bank/activeBanks`
- `GET /api/v1/bank/deactivatedBanks`

## Fraud Scoring

VaultGuard scores transactions from `0.0` to `1.0`.

| Score Range | Status | Meaning |
|---|---|---|
| `0.0 – 0.69` | COMPLETED | Normal transaction |
| `0.7 – 1.0` | FLAGGED | Suspicious transaction requiring review |

### Signals Used

- Transaction amount anomalies
- Transfer velocity
- Geo-location changes
- Rate-limit and request behavior

## Rate Limiting

VaultGuard uses Redis-backed Bucket4j rate limiting for:

- IP-based limits
- User-based limits

Configure the values in `backend/.env` or `backend/.env.docker`.

## Useful Notes

- The backend is configured to use the `dev` profile for local seeded data.
- The frontend API client reads `VITE_API_BASE_URL` from its env file.
- If you change database credentials or container hostnames, update the copied env files before starting Docker.

## Directory Layout

```text
backend/src/main/java/com/guard/vaultguard
├── config
├── controllers
├── dto
├── entities
├── exceptions
├── kafka
├── repositories
├── security
└── service
```

