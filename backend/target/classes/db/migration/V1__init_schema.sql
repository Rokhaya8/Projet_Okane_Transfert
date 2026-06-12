CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    user_type       VARCHAR(31),
    full_name       VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    phone           VARCHAR(50)  NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    last_login      TIMESTAMP,
    role            VARCHAR(50)  NOT NULL,
    agency_id       BIGINT
);

CREATE TABLE IF NOT EXISTS agencies (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    address         VARCHAR(500) NOT NULL,
    country         VARCHAR(100) NOT NULL,
    daily_limit     NUMERIC(19, 2) NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    manager_id      BIGINT REFERENCES users(id)
);

ALTER TABLE users
    ADD CONSTRAINT fk_users_agency
    FOREIGN KEY (agency_id) REFERENCES agencies(id);

CREATE TABLE IF NOT EXISTS currencies (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(10)  NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    symbol          VARCHAR(10)  NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS transfer_corridors (
    id                      BIGSERIAL PRIMARY KEY,
    source_country          VARCHAR(100) NOT NULL,
    destination_country     VARCHAR(100) NOT NULL,
    source_currency_id      BIGINT REFERENCES currencies(id),
    destination_currency_id BIGINT REFERENCES currencies(id),
    active                  BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS beneficiaries (
    id              BIGSERIAL PRIMARY KEY,
    full_name       VARCHAR(255) NOT NULL,
    phone           VARCHAR(50)  NOT NULL,
    country         VARCHAR(100) NOT NULL,
    identity_number VARCHAR(100) NOT NULL,
    watchlist_flag  BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS fee_grids (
    id              BIGSERIAL PRIMARY KEY,
    corridor_id     BIGINT REFERENCES transfer_corridors(id),
    valid_from      TIMESTAMP    NOT NULL,
    valid_to        TIMESTAMP    NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS fee_tiers (
    id                      BIGSERIAL PRIMARY KEY,
    fee_grid_id             BIGINT REFERENCES fee_grids(id),
    min_amount              NUMERIC(19, 2) NOT NULL,
    max_amount              NUMERIC(19, 2) NOT NULL,
    fixed_fee               NUMERIC(19, 2) NOT NULL,
    percentage_fee          NUMERIC(8, 4)  NOT NULL,
    agency_share_percent    NUMERIC(8, 4)  NOT NULL
);

CREATE TABLE IF NOT EXISTS transfers (
    id                  BIGSERIAL PRIMARY KEY,
    reference_code      VARCHAR(50)  NOT NULL UNIQUE,
    amount_sent         NUMERIC(19, 2) NOT NULL,
    amount_received     NUMERIC(19, 2) NOT NULL,
    fees                NUMERIC(19, 2) NOT NULL,
    commission_agency   NUMERIC(19, 2) NOT NULL,
    commission_central  NUMERIC(19, 2) NOT NULL,
    status              VARCHAR(50)  NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    paid_at             TIMESTAMP,
    expiry_date         TIMESTAMP,
    agent_id            BIGINT REFERENCES users(id),
    source_agency_id    BIGINT REFERENCES agencies(id),
    destination_agency_id BIGINT REFERENCES agencies(id),
    corridor_id         BIGINT REFERENCES transfer_corridors(id),
    client_id           BIGINT REFERENCES users(id),
    beneficiary_id      BIGINT REFERENCES beneficiaries(id)
);

CREATE TABLE IF NOT EXISTS cash_drawers (
    id                  BIGSERIAL PRIMARY KEY,
    agent_id            BIGINT       NOT NULL REFERENCES users(id),
    agency_id           BIGINT       NOT NULL REFERENCES agencies(id),
    opening_balance     NUMERIC(19, 2) NOT NULL DEFAULT 0,
    current_balance     NUMERIC(19, 2) NOT NULL DEFAULT 0,
    closing_balance     NUMERIC(19, 2),
    status              VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    opened_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    closed_at           TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_cash_drawers_agency ON cash_drawers(agency_id);
CREATE INDEX IF NOT EXISTS idx_cash_drawers_agent  ON cash_drawers(agent_id);

CREATE TABLE IF NOT EXISTS sensitive_operations (
    id                  BIGSERIAL PRIMARY KEY,
    operation_type      VARCHAR(50)  NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    transfer_id         BIGINT       REFERENCES transfers(id),
    requested_by_id     BIGINT       NOT NULL REFERENCES users(id),
    agency_id           BIGINT       NOT NULL REFERENCES agencies(id),
    processed_by_id     BIGINT       REFERENCES users(id),
    rejection_reason    TEXT,
    amount              NUMERIC(19, 2),
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    processed_at        TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sensitive_ops_agency_status ON sensitive_operations(agency_id, status);

CREATE TABLE IF NOT EXISTS exchange_rates (
    id              BIGSERIAL PRIMARY KEY,
    currency_id     BIGINT REFERENCES currencies(id),
    rate            NUMERIC(19, 6) NOT NULL,
    effective_date  TIMESTAMP    NOT NULL DEFAULT NOW(),
    source          VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT REFERENCES users(id),
    action          VARCHAR(100) NOT NULL,
    entity_type     VARCHAR(100) NOT NULL,
    entity_id       BIGINT,
    details         TEXT,
    ip_address      VARCHAR(50)  NOT NULL,
    timestamp       TIMESTAMP    NOT NULL DEFAULT NOW()
);
