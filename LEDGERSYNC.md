# LedgerSync: AI Agent System Context
**Status:** Architecture Phase (Java 25 / Spring Boot 3.5)

## 1. Project Identity & Goal
- **App Name:** LedgerSync
- **Mission:** High-volume reconciliation of internal merchant payments vs. external bank settlements.
- **Tone:** Enterprise FinTech, ACID compliant, performance-focused.

## 2. Tech Stack (2025 Standard)
- **JDK:** 21 (LTS) [Targeting 25 features]
- **Framework:** Spring Boot 3.5+, Spring Batch, Spring Cloud Stream.
- **Database:** PostgreSQL (Postgres 17 syntax).
- **Messaging:** Google Pub/Sub (via Spring Cloud GCP).

## 3. Database Schema (Source of Truth)
### Table: ls_payment_log (Internal)
Columns: internal_id (UUID), gateway_reference (Key), amount (Numeric 19,4), currency, transaction_date, gateway_provider.

### Table: ls_settlement_entry (External)
Columns: id (UUID), source_provider, rrn, auth_code, request_id, gross_amount, net_amount, fee_amount, recon_status.

### Table: ls_recon_result (Matching)
Columns: id (UUID), settlement_entry_id, payment_log_id, match_status, variance_amount.

## 4. Coding Instructions for Agent
- Use **Java 25 Primitive Patterns** for switch statements in parsers.
- Use **Flexible Constructor Bodies** for DTO validation.
- Every Service must have an Interface.
- All Batch jobs must be idempotent (skip/retry logic included).
- All Batch jobs must be idempotent (skip/retry logic included).
- **No Lombok**: Use manual getters/setters/constructors due to Java 25 incompatibility.