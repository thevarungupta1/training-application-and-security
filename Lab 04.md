# Lab 04: PostgreSQL Mortgage Domain Modeling and Query Optimization

**Audience:** Developers and data engineers in the mortgage-platform training  
**Platform:** PostgreSQL 15+ (or organization-approved equivalent), VS Code, psql or pgAdmin  
**Focus:** Relational schema design, borrower-loan modeling, and mortgage-query performance tuning  
**Estimated time:** 2.5 to 3 hours  
**Last reviewed:** 8 September 2026

## Purpose

In this lab, you will design and validate a mortgage servicing relational model in PostgreSQL, then optimize query performance for common servicing workflows. You will:

- design a PostgreSQL schema for a mortgage servicing system;
- create a borrower-to-loan relationship model with clear cardinality and integrity constraints;
- write optimized mortgage queries for operational and servicing use cases;
- improve a deliberately slow mortgage search query with indexing; and
- finalize a baseline relational model for the mortgage domain capstone.

This lab focuses on data architecture and SQL behavior. It does not require application-service implementation.

## Completion criteria

The lab is complete when you can show:

- a normalized mortgage servicing schema with primary keys, foreign keys, and essential constraints;
- a borrower-to-loan model that supports one borrower with multiple loans and co-borrower scenarios;
- optimized SQL queries for loan lookup, delinquency detection, and servicing dashboard metrics;
- a measured performance improvement for the mini exercise slow query after index tuning; and
- capstone evidence that the baseline relational model is finalized for the mortgage domain.

## Operating model and context

Use a dedicated training database and keep all schema objects in a single schema namespace (for example, `mortgage_lab`) to simplify cleanup and review.

```mermaid
erDiagram
    BORROWER ||--o{ BORROWER_LOAN : linked_to
    LOAN ||--o{ BORROWER_LOAN : linked_to
    LOAN ||--o{ PAYMENT_SCHEDULE : has
    LOAN ||--o{ SERVICING_EVENT : records

    BORROWER {
        bigint borrower_id PK
        text first_name
        text last_name
        text email
        date date_of_birth
        text ssn_last4
    }

    LOAN {
        bigint loan_id PK
        text loan_number
        numeric original_principal
        numeric current_balance
        numeric interest_rate
        date origination_date
        text loan_status
        date next_due_date
    }

    BORROWER_LOAN {
        bigint borrower_loan_id PK
        bigint borrower_id FK
        bigint loan_id FK
        text borrower_role
        date relationship_start_date
    }

    PAYMENT_SCHEDULE {
        bigint payment_schedule_id PK
        bigint loan_id FK
        date due_date
        numeric due_amount
        date paid_date
        text payment_status
    }

    SERVICING_EVENT {
        bigint servicing_event_id PK
        bigint loan_id FK
        text event_type
        timestamp event_ts
        text event_notes
    }
```

---

## Hands-on Lab

### 0. Set up PostgreSQL with Docker and verify connection

Start PostgreSQL in a local Docker container so all remaining steps run against the same database instance.

```powershell
docker pull postgres:16

docker run --name mortgage-pg \
    -e POSTGRES_USER=mortgage_user \
    -e POSTGRES_PASSWORD=mortgage_pass \
    -e POSTGRES_DB=mortgage_servicing \
    -p 5432:5432 \
    -d postgres:16
```

If a previous container with the same name already exists, start it:

```powershell
docker start mortgage-pg
```

Verify container health and port binding:

```powershell
docker ps --filter "name=mortgage-pg"
docker logs mortgage-pg --tail 30
```

Wait until logs show PostgreSQL is ready to accept connections.

Connect from the host using psql:

```powershell
psql -h localhost -p 5432 -U mortgage_user -d mortgage_servicing
```

When prompted, enter password: `mortgage_pass`

If `psql` is not installed on the host, connect from inside the container:

```powershell
docker exec -it mortgage-pg bash
psql -U mortgage_user -d mortgage_servicing
```

Alternative single command without opening an interactive shell:

```powershell
docker exec -it mortgage-pg psql -U mortgage_user -d mortgage_servicing
```

Run a smoke check in psql:

```sql
SELECT current_database(), current_user, version();
```

Expected outcome:

- container `mortgage-pg` is running;
- PostgreSQL is reachable on `localhost:5432`; and
- psql connection succeeds to database `mortgage_servicing`.

Continue with Step 1 using the same active connection.

### 1. Create schema foundation for mortgage servicing

Create a schema and core tables with integrity rules.

```sql
CREATE SCHEMA IF NOT EXISTS mortgage_lab;
SET search_path TO mortgage_lab;

CREATE TABLE IF NOT EXISTS borrower (
    borrower_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    date_of_birth DATE NOT NULL,
    ssn_last4 CHAR(4) NOT NULL CHECK (ssn_last4 ~ '^[0-9]{4}$')
);

CREATE TABLE IF NOT EXISTS loan (
    loan_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    loan_number TEXT NOT NULL UNIQUE,
    original_principal NUMERIC(14,2) NOT NULL CHECK (original_principal > 0),
    current_balance NUMERIC(14,2) NOT NULL CHECK (current_balance >= 0),
    interest_rate NUMERIC(5,3) NOT NULL CHECK (interest_rate > 0),
    origination_date DATE NOT NULL,
    loan_status TEXT NOT NULL CHECK (loan_status IN ('ACTIVE', 'PAID_OFF', 'DELINQUENT', 'FORBEARANCE')),
    next_due_date DATE
);

CREATE TABLE IF NOT EXISTS borrower_loan (
    borrower_loan_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    borrower_id BIGINT NOT NULL REFERENCES borrower(borrower_id) ON DELETE RESTRICT,
    loan_id BIGINT NOT NULL REFERENCES loan(loan_id) ON DELETE RESTRICT,
    borrower_role TEXT NOT NULL CHECK (borrower_role IN ('PRIMARY', 'CO_BORROWER')),
    relationship_start_date DATE NOT NULL,
    UNIQUE (borrower_id, loan_id)
);

CREATE TABLE IF NOT EXISTS payment_schedule (
    payment_schedule_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    loan_id BIGINT NOT NULL REFERENCES loan(loan_id) ON DELETE CASCADE,
    due_date DATE NOT NULL,
    due_amount NUMERIC(12,2) NOT NULL CHECK (due_amount > 0),
    paid_date DATE,
    payment_status TEXT NOT NULL CHECK (payment_status IN ('DUE', 'PAID', 'LATE', 'PARTIAL'))
);

CREATE TABLE IF NOT EXISTS servicing_event (
    servicing_event_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    loan_id BIGINT NOT NULL REFERENCES loan(loan_id) ON DELETE CASCADE,
    event_type TEXT NOT NULL,
    event_ts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    event_notes TEXT
);
```

Validation checks:

- each table has a surrogate primary key;
- required fields are `NOT NULL`;
- borrower-loan duplicates are prevented;
- loan and payment statuses are constrained to valid domain values.

### 2. Build borrower-to-loan relationship model

Insert sample records to validate cardinality and role handling.

```sql
INSERT INTO borrower (first_name, last_name, email, date_of_birth, ssn_last4)
VALUES
('Ava', 'Martin', 'ava.martin@example.com', '1988-06-14', '2241'),
('Noah', 'Rivera', 'noah.rivera@example.com', '1985-11-02', '9083'),
('Mia', 'Chen', 'mia.chen@example.com', '1990-03-21', '7710');

INSERT INTO loan (loan_number, original_principal, current_balance, interest_rate, origination_date, loan_status, next_due_date)
VALUES
('LN-100001', 420000.00, 396450.33, 6.125, '2024-01-18', 'ACTIVE', '2026-10-01'),
('LN-100002', 310000.00, 305900.10, 5.875, '2025-04-10', 'ACTIVE', '2026-10-01');

INSERT INTO borrower_loan (borrower_id, loan_id, borrower_role, relationship_start_date)
VALUES
(1, 1, 'PRIMARY', '2024-01-18'),
(2, 1, 'CO_BORROWER', '2024-01-18'),
(3, 2, 'PRIMARY', '2025-04-10');
```

Model verification query:

```sql
SELECT
    l.loan_number,
    b.borrower_id,
    b.first_name,
    b.last_name,
    bl.borrower_role
FROM borrower_loan bl
JOIN borrower b ON b.borrower_id = bl.borrower_id
JOIN loan l ON l.loan_id = bl.loan_id
ORDER BY l.loan_number, bl.borrower_role;
```

Expected interpretation:

- one loan can map to multiple borrowers (`PRIMARY`, `CO_BORROWER`);
- one borrower can participate in one or more loans over time;
- role semantics are explicit in the junction table.

### 3. Write optimized mortgage queries

Use the following operational query patterns.

#### Query A: Borrower portfolio view

```sql
SELECT
    b.borrower_id,
    b.first_name,
    b.last_name,
    l.loan_number,
    l.current_balance,
    l.loan_status,
    l.next_due_date
FROM borrower b
JOIN borrower_loan bl ON bl.borrower_id = b.borrower_id
JOIN loan l ON l.loan_id = bl.loan_id
WHERE b.email = 'ava.martin@example.com'
ORDER BY l.next_due_date;
```

#### Query B: Delinquency candidate detection

```sql
SELECT
    l.loan_number,
    l.current_balance,
    ps.due_date,
    ps.payment_status
FROM loan l
JOIN payment_schedule ps ON ps.loan_id = l.loan_id
WHERE ps.payment_status IN ('LATE', 'DUE')
  AND ps.due_date < CURRENT_DATE
  AND l.loan_status = 'ACTIVE'
ORDER BY ps.due_date ASC;
```

#### Query C: Recent servicing timeline per loan

```sql
SELECT
    l.loan_number,
    se.event_type,
    se.event_ts,
    se.event_notes
FROM loan l
JOIN servicing_event se ON se.loan_id = l.loan_id
WHERE l.loan_number = 'LN-100001'
ORDER BY se.event_ts DESC
LIMIT 20;
```

Add performance-supporting indexes for the query patterns above.

```sql
CREATE INDEX IF NOT EXISTS idx_borrower_email ON borrower (email);
CREATE INDEX IF NOT EXISTS idx_borrower_loan_borrower_id ON borrower_loan (borrower_id);
CREATE INDEX IF NOT EXISTS idx_borrower_loan_loan_id ON borrower_loan (loan_id);
CREATE INDEX IF NOT EXISTS idx_loan_status_due_date ON loan (loan_status, next_due_date);
CREATE INDEX IF NOT EXISTS idx_payment_schedule_loan_status_due ON payment_schedule (loan_id, payment_status, due_date);
CREATE INDEX IF NOT EXISTS idx_servicing_event_loan_ts ON servicing_event (loan_id, event_ts DESC);
```

Use `EXPLAIN (ANALYZE, BUFFERS)` on each query and capture planning and execution timing before and after indexing when data volume is sufficient.

### 4. Load realistic mortgage-scale sample data for performance testing

Use generated data so query tuning reflects realistic table sizes.

```sql
INSERT INTO borrower (first_name, last_name, email, date_of_birth, ssn_last4)
SELECT
    'Borrower' || gs,
    'Last' || gs,
    'borrower' || gs || '@example.com',
    DATE '1970-01-01' + ((gs % 15000) * INTERVAL '1 day'),
    LPAD((1000 + (gs % 9000))::text, 4, '0')
FROM generate_series(100, 5100) gs;

INSERT INTO loan (loan_number, original_principal, current_balance, interest_rate, origination_date, loan_status, next_due_date)
SELECT
    'LN-' || TO_CHAR(gs, 'FM000000'),
    150000 + (gs % 400000),
    120000 + (gs % 350000),
    4.500 + ((gs % 250) / 100.0),
    DATE '2018-01-01' + ((gs % 2500) * INTERVAL '1 day'),
    CASE WHEN gs % 12 = 0 THEN 'DELINQUENT' ELSE 'ACTIVE' END,
    CURRENT_DATE + ((gs % 45) * INTERVAL '1 day')
FROM generate_series(100, 10100) gs;

INSERT INTO borrower_loan (borrower_id, loan_id, borrower_role, relationship_start_date)
SELECT
    b.borrower_id,
    l.loan_id,
    'PRIMARY',
    l.origination_date
FROM borrower b
JOIN loan l ON l.loan_id = b.borrower_id
WHERE b.borrower_id >= 100;
```

Optional co-borrower distribution:

```sql
INSERT INTO borrower_loan (borrower_id, loan_id, borrower_role, relationship_start_date)
SELECT
    b2.borrower_id,
    l.loan_id,
    'CO_BORROWER',
    l.origination_date
FROM loan l
JOIN borrower b2 ON b2.borrower_id = l.loan_id + 1
WHERE l.loan_id % 5 = 0;
```

Data quality checks:

```sql
SELECT COUNT(*) AS borrower_count FROM borrower;
SELECT COUNT(*) AS loan_count FROM loan;
SELECT borrower_role, COUNT(*) FROM borrower_loan GROUP BY borrower_role ORDER BY borrower_role;
```

### 5. Validate query plans and document optimization evidence

Capture before/after execution evidence for at least two critical servicing queries.

```sql
EXPLAIN (ANALYZE, BUFFERS, VERBOSE)
SELECT
    l.loan_number,
    l.current_balance,
    ps.due_date,
    ps.payment_status
FROM loan l
JOIN payment_schedule ps ON ps.loan_id = l.loan_id
WHERE ps.payment_status IN ('LATE', 'DUE')
  AND ps.due_date < CURRENT_DATE
  AND l.loan_status = 'ACTIVE'
ORDER BY ps.due_date ASC;
```

Evidence checklist:

- baseline plan shows sequential or high-cost scans;
- tuned plan shows effective index usage;
- measured runtime and buffer reads are reduced;
- result set correctness is unchanged after tuning.

---

## Mini Exercise

### Optimize slow mortgage search query using indexes

Start from this intentionally slow pattern:

```sql
SELECT
    l.loan_number,
    b.first_name,
    b.last_name,
    l.current_balance,
    l.loan_status
FROM loan l
JOIN borrower_loan bl ON bl.loan_id = l.loan_id
JOIN borrower b ON b.borrower_id = bl.borrower_id
WHERE LOWER(b.last_name) LIKE '%ri%'
  AND l.loan_status = 'ACTIVE'
ORDER BY l.current_balance DESC;
```

Task:

- identify the bottleneck with `EXPLAIN (ANALYZE, BUFFERS)`;
- design index strategy to reduce full scans;
- re-run explain analyze and compare execution time.

Suggested optimization path:

```sql
CREATE INDEX IF NOT EXISTS idx_borrower_last_name_lower
ON borrower ((LOWER(last_name)));

CREATE INDEX IF NOT EXISTS idx_loan_status_balance
ON loan (loan_status, current_balance DESC);
```

Stretch refinement:

- replace `%ri%` with `ri%` when business allows prefix-only search;
- evaluate `pg_trgm` for contains-search patterns that cannot use standard B-tree efficiently.

Mini exercise success condition:

- the tuned query shows lower execution time and reduced row scans compared with baseline.

### Mini Exercise 2: Add covering index for servicing dashboard query

Scenario:

A servicing dashboard frequently reads active loans sorted by due date while displaying only loan number, status, due date, and balance.

Query:

```sql
SELECT loan_number, loan_status, next_due_date, current_balance
FROM loan
WHERE loan_status = 'ACTIVE'
ORDER BY next_due_date
LIMIT 100;
```

Task:

- design a covering index for this access pattern;
- verify plan improvement with `EXPLAIN (ANALYZE, BUFFERS)`;
- confirm identical functional output.

Suggested starting point:

```sql
CREATE INDEX IF NOT EXISTS idx_loan_active_due_cover
ON loan (loan_status, next_due_date)
INCLUDE (loan_number, current_balance);
```

### Mini Exercise 3: Compare functional index vs prefix search strategy

Scenario:

Operations wants fast borrower lookup by last name from a type-ahead UI.

Tasks:

- compare `LOWER(last_name) LIKE '%ri%'` and `LOWER(last_name) LIKE 'ri%'`;
- measure both variants with `EXPLAIN (ANALYZE, BUFFERS)`;
- explain when to keep a functional B-tree index and when to consider trigram indexing.

Expected conclusion:

- prefix search (`ri%`) generally benefits from B-tree functional indexes;
- contains search (`%ri%`) often needs trigram (`pg_trgm`) for scalable performance.

---

## Capstone Progress

### Baseline relational model finalized for mortgage domain

Deliver and review these capstone artifacts:

- final ER model with entities, keys, and relationship cardinalities;
- DDL script containing borrower, loan, borrower_loan, payment_schedule, and servicing_event tables;
- index strategy document mapped to key mortgage servicing queries;
- query evidence pack containing at least one before/after `EXPLAIN ANALYZE` result; and
- assumptions log for future extensions (escrow, property, servicing transfers, investor reporting).

Capstone checkpoint statement:

The baseline relational model is finalized for the mortgage domain and is ready for application-service integration in the next lab.
