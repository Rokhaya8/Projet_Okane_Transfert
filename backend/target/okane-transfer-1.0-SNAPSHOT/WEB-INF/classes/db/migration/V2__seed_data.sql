-- ============================================
-- DONNÉES DE TEST — OkaneTransfer
-- À exécuter dans pgAdmin ou psql
-- sur la base : okane_transfer
-- ============================================

-- Devises
INSERT INTO currencies (code, name, symbol, active) VALUES
('MAD', 'Dirham Marocain', 'DH', true),
('EUR', 'Euro', '€', true),
('USD', 'Dollar Américain', '$', true),
('XOF', 'Franc CFA', 'FCFA', true),
('DZD', 'Dinar Algérien', 'DA', true)
ON CONFLICT (code) DO NOTHING;

-- Agences
INSERT INTO agencies (name, address, country, dailylimit, active, createdat, manager_id) VALUES
('Agence Okane Casablanca', '12 Rue Hassan II, Casablanca', 'Maroc', 500000.00, true, NOW(), NULL),
('Agence Okane Rabat', '5 Avenue Mohammed V, Rabat', 'Maroc', 300000.00, true, NOW(), NULL)
ON CONFLICT DO NOTHING;

-- Utilisateurs (mot de passe = "password123" hashé en BCrypt)
INSERT INTO users (fullname, email, password, phone, active, createdat, role, user_type) VALUES
('Admin Okane', 'admin@okane.ma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lBy2', '0600000001', true, NOW(), 'ROLE_ADMIN', 'ADMIN'),
('Manager Casablanca', 'manager@okane.ma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lBy2', '0600000002', true, NOW(), 'ROLE_MANAGER', 'MANAGER'),
('Agent Mohamed Alami', 'agent@okane.ma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lBy2', '0600000003', true, NOW(), 'ROLE_AGENT', 'AGENT'),
('Agent Sara Benali', 'agent2@okane.ma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lBy2', '0600000004', true, NOW(), 'ROLE_AGENT', 'AGENT'),
('Client Karim Tazi', 'client@okane.ma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lBy2', '0600000005', true, NOW(), 'ROLE_CLIENT', 'CLIENT')
ON CONFLICT (email) DO NOTHING;

-- Corridors (routes de transfert)
INSERT INTO transfer_corridors (sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'Maroc', 'Sénégal', c1.id, c2.id, true
FROM currencies c1, currencies c2
WHERE c1.code = 'MAD' AND c2.code = 'XOF';

INSERT INTO transfer_corridors (sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'Maroc', 'France', c1.id, c2.id, true
FROM currencies c1, currencies c2
WHERE c1.code = 'MAD' AND c2.code = 'EUR';

INSERT INTO transfer_corridors (sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'Maroc', 'Algérie', c1.id, c2.id, true
FROM currencies c1, currencies c2
WHERE c1.code = 'MAD' AND c2.code = 'DZD';

-- Taux de change
INSERT INTO exchange_rates (currency_id, rate, effectivedate, source)
SELECT id, 57.50, NOW(), 'BAM' FROM currencies WHERE code = 'XOF';

INSERT INTO exchange_rates (currency_id, rate, effectivedate, source)
SELECT id, 0.092, NOW(), 'BAM' FROM currencies WHERE code = 'EUR';

INSERT INTO exchange_rates (currency_id, rate, effectivedate, source)
SELECT id, 3.62, NOW(), 'BAM' FROM currencies WHERE code = 'DZD';

-- Grilles tarifaires
INSERT INTO fee_grids (corridor_id, validfrom, validto, active)
SELECT id, NOW(), NOW() + INTERVAL '1 year', true
FROM transfer_corridors WHERE sourcecountry = 'Maroc' AND destinationcountry = 'Sénégal';

-- Tranches tarifaires (Maroc → Sénégal)
INSERT INTO fee_tiers (fee_grid_id, minamount, maxamount, fixedfee, percentagefee, agencysharepercent)
SELECT fg.id, 0, 500, 15.00, 0.00, 30.00
FROM fee_grids fg
JOIN transfer_corridors tc ON fg.corridor_id = tc.id
WHERE tc.sourcecountry = 'Maroc' AND tc.destinationcountry = 'Sénégal';

INSERT INTO fee_tiers (fee_grid_id, minamount, maxamount, fixedfee, percentagefee, agencysharepercent)
SELECT fg.id, 500, 2000, 25.00, 0.00, 30.00
FROM fee_grids fg
JOIN transfer_corridors tc ON fg.corridor_id = tc.id
WHERE tc.sourcecountry = 'Maroc' AND tc.destinationcountry = 'Sénégal';

INSERT INTO fee_tiers (fee_grid_id, minamount, maxamount, fixedfee, percentagefee, agencysharepercent)
SELECT fg.id, 2000, 10000, 50.00, 0.00, 30.00
FROM fee_grids fg
JOIN transfer_corridors tc ON fg.corridor_id = tc.id
WHERE tc.sourcecountry = 'Maroc' AND tc.destinationcountry = 'Sénégal';

-- Caisse de l'agent (agent id = 3)
INSERT INTO cash_drawers (agent_id, agency_id, balance, status, openingtime)
SELECT u.id, a.id, 15000.00, 'OPEN', NOW()
FROM users u, agencies a
WHERE u.email = 'agent@okane.ma' AND a.name = 'Agence Okane Casablanca';

-- Bénéficiaires
INSERT INTO beneficiaries (fullname, phone, country, identitynumber, watchlistflag) VALUES
('Moussa Diallo', '+221771234567', 'Sénégal', 'SN12345678', false),
('Fatou Sow', '+221779876543', 'Sénégal', 'SN87654321', false),
('Aminata Koné', '+221776543210', 'Sénégal', 'SN11223344', false),
('Jean Dupont', '+33612345678', 'France', 'FR99887766', false),
('Ahmed Benali', '+213661234567', 'Algérie', 'DZ55443322', false);

-- Transferts de test
INSERT INTO transfers (referencecode, amountsent, amountreceived, fees, commissionagency, commissioncentral, status, createdat, expirydate, agent_id, agency_id, corridor_id, beneficiary_id)
SELECT 'AB4X9K2M', 1000.00, 57500.00, 25.00, 7.50, 17.50, 'PENDING',
       NOW() - INTERVAL '2 hours', NOW() + INTERVAL '5 days',
       u.id, a.id, tc.id, b.id
FROM users u, agencies a, transfer_corridors tc, beneficiaries b
WHERE u.email = 'agent@okane.ma'
  AND a.name = 'Agence Okane Casablanca'
  AND tc.sourcecountry = 'Maroc' AND tc.destinationcountry = 'Sénégal'
  AND b.phone = '+221771234567';

INSERT INTO transfers (referencecode, amountsent, amountreceived, fees, commissionagency, commissioncentral, status, createdat, paidat, expirydate, agent_id, agency_id, corridor_id, beneficiary_id)
SELECT 'CD8Y7P3Q', 500.00, 28750.00, 15.00, 4.50, 10.50, 'PAID',
       NOW() - INTERVAL '1 day', NOW() - INTERVAL '20 hours', NOW() + INTERVAL '6 days',
       u.id, a.id, tc.id, b.id
FROM users u, agencies a, transfer_corridors tc, beneficiaries b
WHERE u.email = 'agent@okane.ma'
  AND a.name = 'Agence Okane Casablanca'
  AND tc.sourcecountry = 'Maroc' AND tc.destinationcountry = 'Sénégal'
  AND b.phone = '+221779876543';

INSERT INTO transfers (referencecode, amountsent, amountreceived, fees, commissionagency, commissioncentral, status, createdat, expirydate, agent_id, agency_id, corridor_id, beneficiary_id)
SELECT 'EF3Z5N8R', 2000.00, 115000.00, 50.00, 15.00, 35.00, 'PENDING',
       NOW() - INTERVAL '3 hours', NOW() + INTERVAL '4 days',
       u.id, a.id, tc.id, b.id
FROM users u, agencies a, transfer_corridors tc, beneficiaries b
WHERE u.email = 'agent@okane.ma'
  AND a.name = 'Agence Okane Casablanca'
  AND tc.sourcecountry = 'Maroc' AND tc.destinationcountry = 'Sénégal'
  AND b.phone = '+221776543210';

INSERT INTO transfers (referencecode, amountsent, amountreceived, fees, commissionagency, commissioncentral, status, createdat, expirydate, agent_id, agency_id, corridor_id, beneficiary_id)
SELECT 'GH7W2L4T', 3000.00, 276.00, 50.00, 15.00, 35.00, 'CANCELLED',
       NOW() - INTERVAL '2 days', NOW() + INTERVAL '5 days',
       u.id, a.id, tc.id, b.id
FROM users u, agencies a, transfer_corridors tc, beneficiaries b
WHERE u.email = 'agent@okane.ma'
  AND a.name = 'Agence Okane Casablanca'
  AND tc.sourcecountry = 'Maroc' AND tc.destinationcountry = 'France'
  AND b.phone = '+33612345678';

INSERT INTO transfers (referencecode, amountsent, amountreceived, fees, commissionagency, commissioncentral, status, createdat, paidat, expirydate, agent_id, agency_id, corridor_id, beneficiary_id)
SELECT 'IJ9V6M1K', 800.00, 46000.00, 25.00, 7.50, 17.50, 'PAID',
       NOW() - INTERVAL '4 hours', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '3 days',
       u.id, a.id, tc.id, b.id
FROM users u, agencies a, transfer_corridors tc, beneficiaries b
WHERE u.email = 'agent2@okane.ma'
  AND a.name = 'Agence Okane Casablanca'
  AND tc.sourcecountry = 'Maroc' AND tc.destinationcountry = 'Sénégal'
  AND b.phone = '+221771234567';
