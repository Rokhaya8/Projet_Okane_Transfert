-- Create tables for Okane Transfer application

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    user_type VARCHAR(50) NOT NULL,
    fullName VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    active BOOLEAN DEFAULT true,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastLogin TIMESTAMP,
    role VARCHAR(50) NOT NULL
);

-- Agencies table
CREATE TABLE agencies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    country VARCHAR(100),
    dailyLimit NUMERIC(19,2),
    active BOOLEAN DEFAULT true,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    manager_id BIGINT,
    CONSTRAINT fk_agency_manager FOREIGN KEY (manager_id) REFERENCES users(id)
);

-- Beneficiaries table
CREATE TABLE beneficiaries (
    id BIGSERIAL PRIMARY KEY,
    fullName VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    country VARCHAR(100),
    identityNumber VARCHAR(100),
    watchlistFlag BOOLEAN DEFAULT false
);

-- Corridors table
CREATE TABLE corridors (
    id BIGSERIAL PRIMARY KEY,
    sendingCountry VARCHAR(100) NOT NULL,
    receivingCountry VARCHAR(100) NOT NULL,
    exchangeRate NUMERIC(19,4) NOT NULL,
    minAmount NUMERIC(19,2),
    maxAmount NUMERIC(19,2),
    baseFee NUMERIC(19,2),
    percentageFee NUMERIC(5,2),
    active BOOLEAN DEFAULT true
);

-- Agent cash sessions table
CREATE TABLE agent_cash_sessions (
    id BIGSERIAL PRIMARY KEY,
    agent_id BIGINT NOT NULL,
    agency_id BIGINT NOT NULL,
    openingBalance NUMERIC(19,2) NOT NULL,
    currentBalance NUMERIC(19,2) NOT NULL,
    closingBalance NUMERIC(19,2),
    countedAmount NUMERIC(19,2),
    discrepancyAmount NUMERIC(19,2),
    openedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closedAt TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_session_agent FOREIGN KEY (agent_id) REFERENCES users(id),
    CONSTRAINT fk_session_agency FOREIGN KEY (agency_id) REFERENCES agencies(id)
);

-- Transfers table
CREATE TABLE transfers (
    id BIGSERIAL PRIMARY KEY,
    referenceCode VARCHAR(255) UNIQUE NOT NULL,
    amountSent NUMERIC(38,2) NOT NULL,
    amountReceived NUMERIC(38,2) NOT NULL,
    fees NUMERIC(19,2),
    commissionAgency NUMERIC(19,2),
    commissionCentral NUMERIC(19,2),
    commission_agency NUMERIC(19,2),
    commission_central NUMERIC(19,2),
    status VARCHAR(50) NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paidAt TIMESTAMP,
    expiryDate TIMESTAMP,
    agent_id BIGINT,
    paying_agent_id BIGINT,
    agency_id BIGINT,
    paying_agency_id BIGINT,
    corridor_id BIGINT,
    client_id BIGINT,
    beneficiary_id BIGINT,
    CONSTRAINT fk_transfer_agent FOREIGN KEY (agent_id) REFERENCES users(id),
    CONSTRAINT fk_transfer_paying_agent FOREIGN KEY (paying_agent_id) REFERENCES users(id),
    CONSTRAINT fk_transfer_agency FOREIGN KEY (agency_id) REFERENCES agencies(id),
    CONSTRAINT fk_transfer_paying_agency FOREIGN KEY (paying_agency_id) REFERENCES agencies(id),
    CONSTRAINT fk_transfer_corridor FOREIGN KEY (corridor_id) REFERENCES corridors(id),
    CONSTRAINT fk_transfer_client FOREIGN KEY (client_id) REFERENCES users(id),
    CONSTRAINT fk_transfer_beneficiary FOREIGN KEY (beneficiary_id) REFERENCES beneficiaries(id)
);

-- Cash operations table
CREATE TABLE cash_operations (
    id BIGSERIAL PRIMARY KEY,
    cash_session_id BIGINT NOT NULL,
    operationType VARCHAR(255) NOT NULL CHECK (operationType IN ('OPENING','TRANSFER_SENT','TRANSFER_PAID','ADJUSTMENT','CLOSING')),
    amount NUMERIC(19,2) NOT NULL,
    balanceBefore NUMERIC(19,2) NOT NULL,
    balanceAfter NUMERIC(19,2) NOT NULL,
    operationDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reference VARCHAR(255) NOT NULL,
    transfer_id BIGINT,
    CONSTRAINT fk_cashop_session FOREIGN KEY (cash_session_id) REFERENCES agent_cash_sessions(id),
    CONSTRAINT fk_cashop_transfer FOREIGN KEY (transfer_id) REFERENCES transfers(id)
);

-- Transfer payments table
CREATE TABLE transfer_payments (
    id BIGSERIAL PRIMARY KEY,
    transfer_id BIGINT NOT NULL,
    agent_id BIGINT NOT NULL,
    agency_id BIGINT NOT NULL,
    beneficiaryIdentityNumber VARCHAR(100),
    paidAmount NUMERIC(19,2) NOT NULL,
    paidAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    receiptNumber VARCHAR(255),
    CONSTRAINT fk_payment_transfer FOREIGN KEY (transfer_id) REFERENCES transfers(id),
    CONSTRAINT fk_payment_agent FOREIGN KEY (agent_id) REFERENCES users(id),
    CONSTRAINT fk_payment_agency FOREIGN KEY (agency_id) REFERENCES agencies(id)
);

-- Audit logs table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entityType VARCHAR(255) NOT NULL,
    entityId BIGINT,
    action VARCHAR(100) NOT NULL,
    performedBy BIGINT,
    performedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ipAddress VARCHAR(255) NOT NULL,
    details TEXT,
    CONSTRAINT fk_audit_user FOREIGN KEY (performedBy) REFERENCES users(id)
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_transfers_reference ON transfers(referenceCode);
CREATE INDEX idx_transfers_status ON transfers(status);
CREATE INDEX idx_transfers_agent ON transfers(agent_id);
CREATE INDEX idx_cash_sessions_agent ON agent_cash_sessions(agent_id);
CREATE INDEX idx_cash_operations_session ON cash_operations(cash_session_id);
