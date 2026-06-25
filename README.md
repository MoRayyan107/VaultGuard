# VaultGuard

An event-driven engine that screens financial transactions for fraud and calculates risk scores in real-time.

## Overview
VaultGuard is a real-time fraud detection service built to handle transaction monitoring at scale. When a payment gateway or client app submits a transaction, VaultGuard authorizes it, applies rate limiting, and hands it off to an asynchronous pipeline. High-risk transactions are flagged instantly, saved to the database, and pushed to an operational dashboard using WebSockets so risk analysts can take action immediately without polling.

### Request Lifecycle

```text
Payment Gateway / Client
        ↓
Filters (CORS → JWT Auth → Redis Rate Limiter)
        ↓
Controller (REST Ingestion)
        ↓
Service Layer (Validation & Routing)
        ↓
Kafka Producer (`transactions-in` topic)
        ↓
Kafka Consumer (Fraud Rules Engine)
        ↓
Risk Evaluation Pipeline (Result: FLAGGED or COMPLETED)
        ↓
Database Commit (PostgreSQL) + Live Alert (WebSockets)
```

### Technical Highlights
- **Decoupled Processing:** Ingestion controllers don't wait for heavy fraud rules to finish. They drop the payload into Kafka and return an immediate acknowledgment, keeping API response times incredibly fast.
- **Distributed Quota Enforcement:** Uses a shared Redis cache for rate limiting. This ensures a client's API limits are enforced accurately even if requests are split across multiple running server instances.
- **Push-Based Dashboarding:** Instead of forcing front-end dashboards to hammer the server with refresh intervals, flagged anomalies are streamed down a WebSocket channel the millisecond they are detected.

## Tech Stack
- **Backend Core:** Java 21 / Spring Boot 3.2.5
- **Security:** Spring Security + Stateless JWT Validation
- **Data & Caching:** PostgreSQL (Production Ledger), H2 (Local Mocking), Redis (Rate Limiting Counters)
- **Event Streaming:** Apache Kafka
- **Real-Time Layer:** Spring WebSocket with STOMP sub-protocol
- **Tooling:** Lombok, Docker & Docker Compose
- **Deployment:** Azure Container Apps

## Getting Started

### Prerequisites
- Java 21 JDK
- Maven 3.9+
- Docker Desktop

### Quick Start (Local Run)

1. **Clone and enter the project:**
```bash
git clone https://github.com/MoRayyan107/VaultGuard
cd VaultGuard
```

2. **Run the Docker compose file:**
```bash
docker compose up -d
```

3. **Launch the application with production configurations: (No Data Seeding)**
```bash
mvn spring-boot:run
```

4. **Launch with dev profile (Data Seeding):**
```bash
mvn clean spring-boot:run "-Dspring-boot.run.profiles=dev"
```

### Transaction Endpoints
*(Endpoints currently in development)*

| Method | Endpoint | Description | Access |
|:---:|:---|:---|:---|
| POST | `/api/v1/fraudDetect/processTransaction` | Submit a new transaction for evaluation | Public |
| POST | `/api/auth/login` | Authenticate and retrieve JWT token | Public |
| POST | `/api/auth/register` | Register a new user | Public |
| GET | `/api/v1/fraudDetect/fetch/flaggedTransactions` | Retrieve all flagged transactions | Admin/Analyst |
| GET | `/api/v1/fraudDetect/fetch/highRiskTransactions` | Retrieve all transactions with a risk score greater than 0.7 | Admin/Analyst |
| GET | `/api/v1/fraudDetect/fetch/transaction/{transactionId}` | Retrieve a specific transaction by transaction ID | Admin/Analyst |
| GET | `/api/v1/fraudDetect/fetch/allTransactions` | Retrieve all transactions | Admin/Analyst |
| GET | `/api/v1/fraudDetect/fetch/transactionById/{id}` | Retrieve a transaction by ID | Admin |

## Risk Scoring Metrics

Evry Transaction is based on Risk Score by range `0.0` to `1.0`

| Score Range | Status | Treatment |
|---|---|---|
| **0.0 – 0.69** | COMPLETED | Normal transaction. Processed and archived. |
| **0.7 – 1.0** | FLAGGED | Suspicious activity. Dispatched to WebSockets for review. |

### Indicators Evaluated:
- **Transaction Velocity:** Tracked via Redis, spike in transaction events.
- **Value Outliers:** if the Transaction amounts are greater than expected.
- **Location Context:** if the user keeps on changing Geo-Location every request in small period.

## Project Directory Layout

```text
com.guard.vaultguard
├── config            # Platform configurations (Kafka, Redis, WebSockets)
├── controller        # HTTP Rest Controllers
├── dto               # Request/Response Data Objects
├── entities          # JPA Database Models & Enums
├── exception         # Centralized error and exception handlers
├── kafka             # Streaming brokers, consumers, and producers
├── repository        # Data Access Objects (JPA Repositories)
├── security          # Token validation and filter setups
└── service           # Core business and fraud evaluation rules
```
