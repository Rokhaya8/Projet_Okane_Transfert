-- Test data. Password for all users: password123
INSERT INTO users (user_type, full_name, email, password, phone, active, role, created_at)
VALUES
('USER', 'Admin Okane', 'admin@okane.com', '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S', '+212600000001', TRUE, 'ROLE_ADMIN', NOW()),
('ROLE_MANAGER', 'Karim Manager', 'manager@okane.com', '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S', '+212600000002', TRUE, 'ROLE_MANAGER', NOW()),
('USER', 'Client Youssef', 'client@okane.com', '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S', '+212600000005', TRUE, 'ROLE_CLIENT', NOW())
ON CONFLICT (email) DO UPDATE
SET user_type = EXCLUDED.user_type,
    role = EXCLUDED.role,
    active = TRUE;

INSERT INTO agencies (name, address, country, daily_limit, active, manager_id, created_at)
SELECT 'Agence Casablanca Centre', '12 Bd Mohammed V, Casablanca', 'Maroc', 500000.00, TRUE, u.id, NOW()
FROM users u
WHERE u.email = 'manager@okane.com'
  AND NOT EXISTS (SELECT 1 FROM agencies a WHERE a.manager_id = u.id);

UPDATE users u
SET agency_id = a.id
FROM agencies a
WHERE u.email = 'manager@okane.com'
  AND a.manager_id = u.id;

INSERT INTO users (user_type, full_name, email, password, phone, active, role, agency_id, matricule, commission_rate, created_at)
SELECT 'ROLE_AGENT', 'Agent Ahmed', 'agent@okane.com',
       '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S',
       '+212600000003', TRUE, 'ROLE_AGENT', a.id, 'AG-001', 2.5, NOW()
FROM agencies a
JOIN users m ON a.manager_id = m.id
WHERE m.email = 'manager@okane.com'
ON CONFLICT (email) DO UPDATE
SET user_type = 'ROLE_AGENT',
    role = 'ROLE_AGENT',
    agency_id = EXCLUDED.agency_id,
    matricule = EXCLUDED.matricule,
    commission_rate = EXCLUDED.commission_rate,
    active = TRUE;

INSERT INTO users (user_type, full_name, email, password, phone, active, role, agency_id, matricule, commission_rate, created_at)
SELECT 'ROLE_AGENT', 'Agent Sara', 'sara.agent@okane.com',
       '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S',
       '+212600000004', TRUE, 'ROLE_AGENT', a.id, 'AG-002', 3.0, NOW()
FROM agencies a
JOIN users m ON a.manager_id = m.id
WHERE m.email = 'manager@okane.com'
ON CONFLICT (email) DO UPDATE
SET user_type = 'ROLE_AGENT',
    role = 'ROLE_AGENT',
    agency_id = EXCLUDED.agency_id,
    matricule = EXCLUDED.matricule,
    commission_rate = EXCLUDED.commission_rate,
    active = TRUE;

INSERT INTO currencies (code, name, symbol, active)
VALUES
('MAD', 'Dirham marocain', 'DH', TRUE),
('EUR', 'Euro', 'EUR', TRUE),
('USD', 'Dollar americain', 'USD', TRUE)
ON CONFLICT (code) DO NOTHING;

INSERT INTO transfer_corridors (source_country, destination_country, source_currency_id, destination_currency_id, active)
SELECT 'Maroc', 'France', mad.id, eur.id, TRUE
FROM currencies mad, currencies eur
WHERE mad.code = 'MAD' AND eur.code = 'EUR'
  AND NOT EXISTS (
      SELECT 1 FROM transfer_corridors c
      WHERE c.source_country = 'Maroc' AND c.destination_country = 'France'
  );

INSERT INTO transfer_corridors (source_country, destination_country, source_currency_id, destination_currency_id, active)
SELECT 'Maroc', 'Etats-Unis', mad.id, usd.id, TRUE
FROM currencies mad, currencies usd
WHERE mad.code = 'MAD' AND usd.code = 'USD'
  AND NOT EXISTS (
      SELECT 1 FROM transfer_corridors c
      WHERE c.source_country = 'Maroc' AND c.destination_country = 'Etats-Unis'
  );

INSERT INTO senders (full_name, phone, country, identity_type, identity_number)
SELECT 'Client Youssef', '+212600000005', 'Maroc', 'CIN', 'MA123456'
WHERE NOT EXISTS (SELECT 1 FROM senders WHERE identity_number = 'MA123456');

INSERT INTO beneficiaries (full_name, phone, country, identity_number, watchlist_flag)
SELECT 'Marie Dupont', '+33600000001', 'France', 'FR123456', FALSE
WHERE NOT EXISTS (SELECT 1 FROM beneficiaries WHERE identity_number = 'FR123456');

INSERT INTO beneficiaries (full_name, phone, country, identity_number, watchlist_flag)
SELECT 'John Smith', '+12025550123', 'Etats-Unis', 'US987654', FALSE
WHERE NOT EXISTS (SELECT 1 FROM beneficiaries WHERE identity_number = 'US987654');

INSERT INTO transfers (
    reference_code, amount_sent, amount_received, fees,
    commission_agency, commission_central, status, reception_mode,
    created_at, paid_at, expiry_date,
    agent_id, agency_id, source_agency_id, destination_agency_id,
    corridor_id, sender_id, client_id, beneficiary_id
)
SELECT
    'OKN-TEST-001', 1000.00, 91.00, 25.00,
    15.00, 10.00, 'PAID', 'CASH_AGENCE',
    NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 day', NOW() + INTERVAL '28 days',
    ag.id, a.id, a.id, a.id,
    c.id, s.id, cl.id, b.id
FROM users ag
JOIN agencies a ON ag.agency_id = a.id
JOIN users cl ON cl.email = 'client@okane.com'
JOIN senders s ON s.identity_number = 'MA123456'
JOIN beneficiaries b ON b.identity_number = 'FR123456'
JOIN transfer_corridors c ON c.destination_country = 'France'
WHERE ag.email = 'agent@okane.com'
ON CONFLICT (reference_code) DO NOTHING;

INSERT INTO transfers (
    reference_code, amount_sent, amount_received, fees,
    commission_agency, commission_central, status, reception_mode,
    created_at, paid_at, expiry_date,
    agent_id, agency_id, source_agency_id, destination_agency_id,
    corridor_id, sender_id, client_id, beneficiary_id
)
SELECT
    'OKN-TEST-002', 2500.00, 245.00, 50.00,
    30.00, 20.00, 'PENDING', 'CASH_AGENCE',
    NOW(), NULL, NOW() + INTERVAL '30 days',
    ag.id, a.id, a.id, a.id,
    c.id, s.id, cl.id, b.id
FROM users ag
JOIN agencies a ON ag.agency_id = a.id
JOIN users cl ON cl.email = 'client@okane.com'
JOIN senders s ON s.identity_number = 'MA123456'
JOIN beneficiaries b ON b.identity_number = 'US987654'
JOIN transfer_corridors c ON c.destination_country = 'Etats-Unis'
WHERE ag.email = 'sara.agent@okane.com'
ON CONFLICT (reference_code) DO NOTHING;

INSERT INTO sensitive_operations (operation_type, status, transfer_id, requested_by_id, agency_id, amount, created_at)
SELECT 'TRANSFER_VALIDATION', 'PENDING', t.id, t.agent_id, t.source_agency_id, t.amount_sent, NOW()
FROM transfers t
WHERE t.reference_code = 'OKN-TEST-002'
  AND NOT EXISTS (SELECT 1 FROM sensitive_operations so WHERE so.transfer_id = t.id);

INSERT INTO cash_drawers (agent_id, agency_id, opening_balance, current_balance, closing_balance, status, opened_at, closed_at)
SELECT ag.id, a.id, 10000.00, 8750.00, NULL, 'OPEN', NOW() - INTERVAL '1 day', NULL
FROM users ag
JOIN agencies a ON ag.agency_id = a.id
WHERE ag.email = 'agent@okane.com'
  AND NOT EXISTS (SELECT 1 FROM cash_drawers cd WHERE cd.agent_id = ag.id AND cd.status = 'OPEN');
