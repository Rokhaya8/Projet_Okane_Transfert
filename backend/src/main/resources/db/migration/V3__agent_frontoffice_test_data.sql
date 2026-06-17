-- =====================================================
-- OKANE TRANSFER - DATA TEST COHERENTE AVEC LES ENTITIES
-- Hibernate ddl-auto=update + PhysicalNamingStrategyStandardImpl
-- Password pour tous : password123
-- =====================================================

TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- =====================================================
-- USERS
-- =====================================================

INSERT INTO users (
    id,
    user_type,
    fullname,
    email,
    password,
    phone,
    active,
    createdAt,
    lastLogin,
    role,
    agency_id,
    matricule,
    commission_rate
)
VALUES
    (
        1,
        'ADMIN',
        'Admin Okane',
        'admin@okane.com',
        '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S',
        '+212600000001',
        true,
        NOW(),
        NULL,
        'ROLE_ADMIN',
        NULL,
        NULL,
        NULL
    ),
    (
        2,
        'ROLE_MANAGER',
        'Karim Manager',
        'manager@okane.com',
        '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S',
        '+212600000002',
        true,
        NOW(),
        NULL,
        'ROLE_MANAGER',
        NULL,
        NULL,
        NULL
    ),
    (
        3,
        'ROLE_AGENT',
        'Agent Ahmed',
        'agent@okane.com',
        '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S',
        '+212600000003',
        true,
        NOW(),
        NULL,
        'ROLE_AGENT',
        NULL,
        'AG-001',
        2.50
    ),
    (
        4,
        'ROLE_AGENT',
        'Agent Sara',
        'sara.agent@okane.com',
        '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S',
        '+212600000004',
        true,
        NOW(),
        NULL,
        'ROLE_AGENT',
        NULL,
        'AG-002',
        3.00
    ),
    (
        5,
        'CLIENT',
        'Client Youssef',
        'client@okane.com',
        '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S',
        '+212600000005',
        true,
        NOW(),
        NULL,
        'ROLE_CLIENT',
        NULL,
        NULL,
        NULL
    );

-- =====================================================
-- AGENCY
-- =====================================================

INSERT INTO agencies (
    id,
    name,
    address,
    country,
    dailyLimit,
    active,
    createdAt,
    manager_id
)
VALUES (
           1,
           'Agence Casablanca Centre',
           '12 Bd Mohammed V, Casablanca',
           'Maroc',
           500000.00,
           true,
           NOW(),
           2
       );

UPDATE users
SET agency_id = 1
WHERE id IN (2, 3, 4);

-- =====================================================
-- CURRENCIES
-- =====================================================
INSERT INTO currencies (
    id,
    code,
    name,
    symbol,
    active
)
VALUES
    (1, 'MAD', 'Dirham marocain', 'DH', true),
    (2, 'EUR', 'Euro', 'EUR', true),
    (3, 'USD', 'Dollar americain', 'USD', true);

-- =====================================================
-- TRANSFER CORRIDORS
-- =====================================================
INSERT INTO transfer_corridors (
    id,
    sourceCountry,
    destinationCountry,
    source_currency_id,
    destination_currency_id,
    active
)
VALUES
    (1, 'Maroc', 'France', 1, 2, true),
    (2, 'Maroc', 'Etats-Unis', 1, 3, true);

-- =====================================================
-- BENEFICIARIES
-- =====================================================
INSERT INTO beneficiaries (
    id,
    fullName,
    phone,
    country,
    identityNumber,
    watchlistFlag
)
VALUES
    (1, 'Marie Dupont', '+33600000001', 'France', 'FR123456', false),
    (2, 'John Smith', '+12025550123', 'Etats-Unis', 'US987654', false);

-- =====================================================
-- TRANSFERS
-- =====================================================
INSERT INTO transfers (
    id,
    referenceCode,
    amountSent,
    amountReceived,
    fees,
    commissionAgency,
    commissionCentral,
    status,
    receptionMode,
    createdAt,
    paidAt,
    expiryDate,
    agent_id,
    paying_agent_id,
    agency_id,
    paying_agency_id,
    source_agency_id,
    destination_agency_id,
    corridor_id,
    client_id,
    beneficiary_id
)
VALUES
    (
        1,
        'OKN-TEST-001',
        1000.00,
        91.00,
        25.00,
        15.00,
        10.00,
        'PAYE',
        'CASH_AGENCE',
        NOW() - INTERVAL '2 days',
        NOW() - INTERVAL '1 day',
        NOW() + INTERVAL '28 days',
        3,
        3,
        1,
        1,
        1,
        1,
        1,
        5,
        1
    ),
    (
        2,
        'OKN-TEST-002',
        2500.00,
        245.00,
        50.00,
        30.00,
        20.00,
        'EN_ATTENTE',
        'CASH_AGENCE',
        NOW(),
        NULL,
        NOW() + INTERVAL '30 days',
        4,
        NULL,
        1,
        NULL,
        1,
        1,
        2,
        5,
        2
    );

-- =====================================================
-- CASH DRAWERS
-- =====================================================

INSERT INTO cash_drawers (
    id,
    agent_id,
    agency_id,
    openingBalance,
    currentBalance,
    closingBalance,
    status,
    openedAt,
    closedAt
)
VALUES
    (
        1,
        3,
        1,
        10000.00,
        8500.00,
        NULL,
        'OPEN',
        NOW() - INTERVAL '3 hours',
        NULL
    ),
    (
        2,
        4,
        1,
        15000.00,
        15000.00,
        NULL,
        'OPEN',
        NOW() - INTERVAL '2 hours',
        NULL
    );

-- =====================================================
-- SENSITIVE OPERATIONS
-- =====================================================

INSERT INTO sensitive_operations (
    id,
    operationType,
    status,
    transfer_id,
    requested_by_id,
    agency_id,
    processed_by_id,
    rejectionReason,
    amount,
    createdAt,
    processedAt
)
VALUES
    (
        1,
        'TRANSFER_VALIDATION',
        'PENDING',
        2,
        4,
        1,
        NULL,
        NULL,
        2500.00,
        NOW(),
        NULL
    );