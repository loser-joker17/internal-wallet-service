# Internal Wallet Service

Transactional wallet system for managing application-specific credits (Gold Coins / Reward Points).  
Built for high data integrity, concurrency safety, and auditability.

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA (Hibernate)
- MySQL
- Flyway (DB migration & seed)
- Maven

---

## System Design

### Entities
- User
- Asset
- Wallet
- Transaction
- LedgerEntry

### Ledger-Based Architecture
Each business transaction produces:
- 1 Debit entry
- 1 Credit entry

Balance is updated atomically and ledger provides audit trail.

---

## Concurrency Strategy

###  Row-Level Locking
Wallet rows are locked using pessimistic locking during updates.

### Deadlock Avoidance
Wallets are always accessed in deterministic order:
smaller wallet_id → larger wallet_id

This prevents circular waits under high load.

### Transactional Safety
Each operation executes in one DB transaction:
- balance update
- transaction record
- ledger entries

---

## Idempotency Strategy

Duplicate operations are prevented using:
unique reference_id constraint

If the same referenceId is received again → request is rejected.

---

## Database Setup

### Create Database
```sql
CREATE DATABASE wallet;
```

---

### Configure application.yaml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wallet
    username: your_username
    password: your_password

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true

  flyway:
    enabled: true
```

---

### Run Seed Script

Execute:

```
init.sql
```

This creates:
- Assets  
- System Treasury Wallet  
- Two Users with initial balances

---

## Run Application

```
mvn spring-boot:run
```

Server starts at:

```
http://localhost:8080
```

---

## API Endpoints

### Process Transaction
POST `/api/wallet/transaction`

Request:
```json
{
  "userId": 2,
  "assetId": 1,
  "amount": 10,
  "referenceId": "txn-001",
  "type": "SPEND"
}
```

Transaction Types:
- TOPUP
- BONUS
- SPEND

---

### Get Wallet Balance
GET `/api/wallet/balance?userId=2&assetId=1`

Response:
```json
{
  "userId": 2,
  "assetId": 1,
  "balance": 90.0000
}
```

---

## Database Schema Overview

### wallets
- user_id
- asset_id
- balance
- version

### transactions
- reference_id (unique)
- type
- status
- amount

### ledger_entries
- wallet_id
- transaction_id
- type 
- amount

---


