# Okane Transfer - Money Transfer Management Platform

Web platform for national and international money transfers, in the spirit of Western
Union or MoneyGram. Agents send and pay out transfers at the counter, managers supervise
their agency, administrators configure the network, and clients follow their transfers
online.

**Live demo:** https://projet-okane-transfert.vercel.app
(the backend runs on a free Render instance: the first request can take up to a minute
while it wakes up)

## The Problem

A transfer network involves several actors with very different needs: the agent at the
counter needs a fast and reliable way to send and pay out money while keeping an exact
cash balance; the manager needs to validate sensitive operations and follow the agency's
activity; the administrator needs to manage agencies, currencies, exchange rates and
fees; and the client wants to know where their money is. Okane Transfer brings these four
workspaces together in one application, with role-based access.

## Features

### Agent workspace
- Create a transfer: sender, beneficiary, destination country, amount, with fees and
  exchange rate computed automatically
- Pay out a transfer, found by reference code or phone number, with a receipt
- Cash management: open and close a cash session, adjustments, balance and history of
  cash operations
- Transaction history

### Manager workspace (agency supervisor)
- Agent management: create, edit, activate, suspend
- Validation of sensitive operations (approve or reject)
- Operations, cash drawers and agent performance
- Reports with Excel and PDF export

### Administrator workspace
- Agencies, currencies and exchange rates
- Transfer corridors and fee grids
- Audit log of sensitive actions
- Dashboard and reports

### Client workspace
- Registration and login
- Dashboard and transfer history
- Transfer tracking by reference code
- Assistant chatbot (Groq API)

## Architecture

```
Angular (Vercel)  ──HTTP + JWT──►  Spring MVC REST API (Tomcat, Render)  ──JPA──►  PostgreSQL (Neon)
```

| Layer | Technologies |
|-------|--------------|
| Frontend | Angular 21, Angular Material, Chart.js |
| Backend | Java 17, Spring MVC 6, Spring Security 6, Spring Data JPA / Hibernate, deployed as a WAR on Tomcat 10 |
| Security | JWT authentication, role-based access (`ADMIN`, `MANAGER`, `AGENT`, `CLIENT`), BCrypt password hashing |
| Database | PostgreSQL, schema versioned with Flyway migrations |
| Other | AOP-based audit logging (`@Auditable`), OpenAPI documentation (springdoc), Groq API for the chatbot |
| Deployment | Docker image (Maven build + Tomcat) on Render, frontend on Vercel |

### Backend structure

```
backend/src/main/java/com/okanetransfer/
├── controller/   REST endpoints (auth, transfers, payouts, cash, agencies, currencies, corridors, manager, audit, chat)
├── service/      Business logic (fees, exchange rates, cash sessions, validations...)
├── entity/       JPA entities (Transfer, Agency, Agent, Client, Currency, FeeGrid, CashDrawer...)
├── repository/   Spring Data JPA repositories
├── dto/          Request and response objects
├── security/     JWT filter and utilities
├── aspect/       Audit logging with AOP
└── config/       Spring MVC, security, CORS, JPA and OpenAPI configuration
```

The Angular application follows the same split: one feature module per workspace in
`frontend/src/app/features/` (`auth`, `agent`, `manager`, `admin`, `client`).

## How to Run Locally

### Requirements
- Java 17, Maven, Tomcat 10
- Node.js and npm
- A PostgreSQL database (local or hosted)

### Backend

Configuration is read from environment variables:

| Variable | Purpose |
|----------|---------|
| `SPRING_DATASOURCE_URL` | JDBC URL of the PostgreSQL database |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | Database credentials |
| `JWT_SECRET` | Key used to sign tokens (at least 32 characters) |
| `groq.api.key` | Groq API key for the chatbot (optional) |

Build the WAR with `mvn clean package` in `backend/`, then deploy it on Tomcat
(for example with the Smart Tomcat plugin in IntelliJ), or build the Docker image with
the provided `Dockerfile`.

### Frontend

```bash
cd frontend
npm install
npm start
```

The application opens on http://localhost:4200. API calls are forwarded to the local
backend through `proxy.conf.json`.

## Context

Academic team project, ENSA Marrakech (2025–2026).
