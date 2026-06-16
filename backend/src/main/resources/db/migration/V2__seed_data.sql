-- ============================================
-- OKANE TRANSFER - SEED DATA
-- ============================================

-- Nettoyage dans l'ordre des clés étrangères
DELETE FROM transfers;
DELETE FROM agencies;
DELETE FROM beneficiaries;
DELETE FROM users;
DELETE FROM transfer_corridors;
DELETE FROM currencies;

-- 1. CURRENCIES
INSERT INTO currencies (id, code, name, symbol, active) VALUES
                                                            (1, 'MAD', 'Dirham Marocain', 'د.م.', true),
                                                            (2, 'EUR', 'Euro', '€', true),
                                                            (3, 'XOF', 'Franc CFA', 'FCFA', true),
                                                            (4, 'USD', 'Dollar Américain', '$', true),
                                                            (5, 'GBP', 'Livre Sterling', '£', true)
    ON CONFLICT (id) DO NOTHING;

-- 2. TRANSFER CORRIDORS
INSERT INTO transfer_corridors (id, sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active) VALUES
                                                                                                                                (1, 'MA', 'SN', 1, 3, true),
                                                                                                                                (2, 'MA', 'FR', 1, 2, true),
                                                                                                                                (3, 'MA', 'ES', 1, 2, true),
                                                                                                                                (4, 'MA', 'BE', 1, 2, true),
                                                                                                                                (5, 'MA', 'US', 1, 4, true)
    ON CONFLICT (id) DO NOTHING;

-- 3. USERS
INSERT INTO users (id, fullname, email, password, phone, active, createdat, role, user_type) VALUES
                                                                                                 (1, 'Admin Systeme',   'admin@fake-okane.ma',           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPZsjxtQ8S2', '0600000001', true, NOW(), 'ROLE_ADMIN',   'ADMIN'),
                                                                                                 (2, 'Karim Benali',    'karim.manager@fake-okane.ma',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPZsjxtQ8S2', '0600000002', true, NOW(), 'ROLE_MANAGER', 'MANAGER'),
                                                                                                 (3, 'Nadia Chraibi',   'nadia.manager@fake-okane.ma',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPZsjxtQ8S2', '0600000003', true, NOW(), 'ROLE_MANAGER', 'MANAGER'),
                                                                                                 (4, 'Omar Filali',     'omar.manager@fake-okane.ma',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPZsjxtQ8S2', '0600000004', true, NOW(), 'ROLE_MANAGER', 'MANAGER'),
                                                                                                 (5, 'Mohamed Alami',   'mohamed.agent@fake-okane.ma',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPZsjxtQ8S2', '0600000010', true, NOW(), 'ROLE_AGENT',   'ROLE_AGENT'),
                                                                                                 (6, 'Aicha Benali',    'aicha.agent@fake-okane.ma',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPZsjxtQ8S2', '0600000011', true, NOW(), 'ROLE_AGENT',   'ROLE_AGENT'),
                                                                                                 (7, 'Youssef Idrissi', 'youssef.agent@fake-okane.ma',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPZsjxtQ8S2', '0600000012', true, NOW(), 'ROLE_AGENT',   'ROLE_AGENT'),
                                                                                                 (8, 'Fatima Ouali',    'fatima.agent@fake-okane.ma',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPZsjxtQ8S2', '0600000013', true, NOW(), 'ROLE_AGENT',   'ROLE_AGENT'),
                                                                                                 (9, 'Hassan Tazi',     'hassan.agent@fake-okane.ma',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPZsjxtQ8S2', '0600000014', true, NOW(), 'ROLE_AGENT',   'ROLE_AGENT'),
                                                                                                 (10, 'Najat Rahali',   'najat.agent@fake-okane.ma',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPZsjxtQ8S2', '0600000015', true, NOW(), 'ROLE_AGENT',   'ROLE_AGENT')
    ON CONFLICT (id) DO NOTHING;

-- 4. AGENCIES
INSERT INTO agencies (id, name, address, country, dailylimit, active, createdat, manager_id) VALUES
                                                                                                 (1, 'Agence Casablanca Centre',  '12 Rue Hassan II, Casablanca',     'MA', 500000.00, true,  NOW(), 2),
                                                                                                 (2, 'Agence Rabat Agdal',        '45 Avenue Fal Ould Oumeir, Rabat', 'MA', 300000.00, true,  NOW(), 3),
                                                                                                 (3, 'Agence Marrakech Gueliz',   '8 Rue de la Liberte, Marrakech',   'MA', 400000.00, true,  NOW(), 4),
                                                                                                 (4, 'Agence Fes Medina',         '23 Talaa Kbira, Fes',              'MA', 250000.00, false, NOW(), NULL),
                                                                                                 (5, 'Agence Tanger Port',        '1 Boulevard Mohammed V, Tanger',   'MA', 350000.00, true,  NOW(), NULL)
    ON CONFLICT (id) DO NOTHING;

-- 5. BENEFICIARIES
INSERT INTO beneficiaries (id, fullname, phone, country, identitynumber, watchlistflag) VALUES
                                                                                            (1, 'Fatou Diallo',     '+221771234567', 'SN', 'SN-2345678', false),
                                                                                            (2, 'Pierre Dupont',    '+33612345678',  'FR', 'FR-9876543', false),
                                                                                            (3, 'Carlos Garcia',    '+34612345678',  'ES', 'ES-1234567', false),
                                                                                            (4, 'Jean Martin',      '+32476123456',  'BE', 'BE-3456789', false),
                                                                                            (5, 'Aminata Traore',   '+221781234567', 'SN', 'SN-1122334', false),
                                                                                            (6, 'Sophie Lefevre',   '+33698765432',  'FR', 'FR-5544332', false),
                                                                                            (7, 'Miguel Torres',    '+34698765432',  'ES', 'ES-7766554', false),
                                                                                            (8, 'Luc Bernard',      '+32471234567',  'BE', 'BE-9988776', false),
                                                                                            (9, 'Modou Fall',       '+221791234567', 'SN', 'SN-6677889', false),
                                                                                            (10, 'Emma Leclerc',    '+33623456789',  'FR', 'FR-2233445', false),
                                                                                            (11, 'Ibrahima Diop',   '+221761234567', 'SN', 'SN-3344556', false),
                                                                                            (12, 'Marie Dubois',    '+33687654321',  'FR', 'FR-6655443', false),
                                                                                            (13, 'Ana Rodriguez',   '+34623456789',  'ES', 'ES-8877665', false),
                                                                                            (14, 'Thomas Leroy',    '+32476654321',  'BE', 'BE-1122334', false),
                                                                                            (15, 'Seydou Kone',     '+221712345678', 'SN', 'SN-9988776', false)
    ON CONFLICT (id) DO NOTHING;

-- 6. TRANSFERS (statuts en FRANÇAIS pour correspondre à l'enum Java)
INSERT INTO transfers (referencecode, amountsent, amountreceived, fees, commissionagency, commissioncentral, status, createdat, paidat, expirydate, agent_id, agency_id, corridor_id, beneficiary_id) VALUES
                                                                                                                                                                                                          ('OK-A1B2C3', 1200.00,  73440.00,  60.00,  30.00, 30.00, 'PAYE',       NOW() - INTERVAL '29 days', NOW() - INTERVAL '29 days', NOW() + INTERVAL '1 day',  5, 1, 1,  1),
                                                                                                                                                                                                          ('OK-D4E5F6', 2500.00,  11500.00,  125.00, 62.50, 62.50, 'PAYE',       NOW() - INTERVAL '29 days', NOW() - INTERVAL '29 days', NOW() + INTERVAL '1 day',  6, 1, 2,  2),
                                                                                                                                                                                                          ('OK-G7H8I9', 800.00,   3680.00,   40.00,  20.00, 20.00, 'PAYE',       NOW() - INTERVAL '28 days', NOW() - INTERVAL '28 days', NOW() + INTERVAL '1 day',  7, 2, 3,  3),
                                                                                                                                                                                                          ('OK-J1K2L3', 3000.00,  13800.00,  150.00, 75.00, 75.00, 'ANNULE',     NOW() - INTERVAL '27 days', NULL,                        NOW() - INTERVAL '20 days', 8, 2, 4,  4),
                                                                                                                                                                                                          ('OK-M4N5O6', 1500.00,  91800.00,  75.00,  37.50, 37.50, 'PAYE',       NOW() - INTERVAL '27 days', NOW() - INTERVAL '27 days', NOW() + INTERVAL '1 day',  9, 3, 1,  5),
                                                                                                                                                                                                          ('OK-P7Q8R9', 4200.00,  19320.00,  210.00, 105.00,105.00,'PAYE',       NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days', NOW() + INTERVAL '1 day', 10, 3, 2,  6),
                                                                                                                                                                                                          ('OK-S1T2U3', 950.00,   4370.00,   47.50,  23.75, 23.75, 'EN_ATTENTE', NOW() - INTERVAL '25 days', NULL,                        NOW() + INTERVAL '5 days', 5, 1, 3,  7),
                                                                                                                                                                                                          ('OK-V4W5X6', 1800.00,  8280.00,   90.00,  45.00, 45.00, 'PAYE',       NOW() - INTERVAL '24 days', NOW() - INTERVAL '24 days', NOW() + INTERVAL '1 day',  6, 1, 4,  8),
                                                                                                                                                                                                          ('OK-Y7Z8A1', 650.00,   39780.00,  32.50,  16.25, 16.25, 'PAYE',       NOW() - INTERVAL '22 days', NOW() - INTERVAL '22 days', NOW() + INTERVAL '1 day',  7, 2, 1,  9),
                                                                                                                                                                                                          ('OK-B2C3D4', 5000.00,  23000.00,  250.00, 125.00,125.00,'ANNULE',     NOW() - INTERVAL '22 days', NULL,                        NOW() - INTERVAL '15 days', 8, 2, 2, 10),
                                                                                                                                                                                                          ('OK-E5F6G7', 2200.00,  134640.00, 110.00, 55.00, 55.00, 'PAYE',       NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days', NOW() + INTERVAL '1 day',  9, 3, 1, 11),
                                                                                                                                                                                                          ('OK-H8I9J1', 3500.00,  16100.00,  175.00, 87.50, 87.50, 'PAYE',       NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days', NOW() + INTERVAL '1 day', 10, 3, 2, 12),
                                                                                                                                                                                                          ('OK-K2L3M4', 700.00,   3220.00,   35.00,  17.50, 17.50, 'PAYE',       NOW() - INTERVAL '19 days', NOW() - INTERVAL '19 days', NOW() + INTERVAL '1 day',  5, 4, 3, 13),
                                                                                                                                                                                                          ('OK-N5O6P7', 1100.00,  67320.00,  55.00,  27.50, 27.50, 'PAYE',       NOW() - INTERVAL '17 days', NOW() - INTERVAL '17 days', NOW() + INTERVAL '1 day',  6, 5, 1, 14),
                                                                                                                                                                                                          ('OK-Q8R9S1', 2800.00,  12880.00,  140.00, 70.00, 70.00, 'EN_ATTENTE', NOW() - INTERVAL '17 days', NULL,                        NOW() + INTERVAL '3 days', 7, 5, 2, 15),
                                                                                                                                                                                                          ('OK-T2U3V4', 900.00,   4140.00,   45.00,  22.50, 22.50, 'PAYE',       NOW() - INTERVAL '16 days', NOW() - INTERVAL '16 days', NOW() + INTERVAL '1 day',  8, 1, 3,  1),
                                                                                                                                                                                                          ('OK-W5X6Y7', 6000.00,  367200.00, 300.00, 150.00,150.00,'PAYE',       NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days', NOW() + INTERVAL '1 day',  9, 1, 1,  2),
                                                                                                                                                                                                          ('OK-Z8A9B1', 1400.00,  6440.00,   70.00,  35.00, 35.00, 'PAYE',       NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days', NOW() + INTERVAL '1 day', 10, 2, 2,  3),
                                                                                                                                                                                                          ('OK-C2D3E4', 500.00,   2300.00,   25.00,  12.50, 12.50, 'ANNULE',     NOW() - INTERVAL '13 days', NULL,                        NOW() - INTERVAL '6 days',  5, 2, 3,  4),
                                                                                                                                                                                                          ('OK-F5G6H7', 3200.00,  195840.00, 160.00, 80.00, 80.00, 'PAYE',       NOW() - INTERVAL '11 days', NOW() - INTERVAL '11 days', NOW() + INTERVAL '1 day',  6, 3, 1,  5),
                                                                                                                                                                                                          ('OK-I8J9K1', 2100.00,  9660.00,   105.00, 52.50, 52.50, 'PAYE',       NOW() - INTERVAL '11 days', NOW() - INTERVAL '11 days', NOW() + INTERVAL '1 day',  7, 3, 2,  6),
                                                                                                                                                                                                          ('OK-L2M3N4', 750.00,   3450.00,   37.50,  18.75, 18.75, 'PAYE',       NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', NOW() + INTERVAL '1 day',  8, 4, 3,  7),
                                                                                                                                                                                                          ('OK-O5P6Q7', 4500.00,  275400.00, 225.00, 112.50,112.50,'PAYE',       NOW() - INTERVAL '8 days',  NOW() - INTERVAL '8 days',  NOW() + INTERVAL '1 day',  9, 4, 1,  8),
                                                                                                                                                                                                          ('OK-R8S9T1', 1650.00,  7590.00,   82.50,  41.25, 41.25, 'EN_ATTENTE', NOW() - INTERVAL '8 days',  NULL,                        NOW() + INTERVAL '2 days', 10, 5, 2,  9),
                                                                                                                                                                                                          ('OK-U2V3W4', 1000.00,  4600.00,   50.00,  25.00, 25.00, 'PAYE',       NOW() - INTERVAL '7 days',  NOW() - INTERVAL '7 days',  NOW() + INTERVAL '1 day',  5, 5, 3, 10),
                                                                                                                                                                                                          ('OK-X5Y6Z7', 2700.00,  165240.00, 135.00, 67.50, 67.50, 'PAYE',       NOW() - INTERVAL '5 days',  NOW() - INTERVAL '5 days',  NOW() + INTERVAL '1 day',  6, 1, 1, 11),
                                                                                                                                                                                                          ('OK-A8B9C1', 3800.00,  17480.00,  190.00, 95.00, 95.00, 'PAYE',       NOW() - INTERVAL '5 days',  NOW() - INTERVAL '5 days',  NOW() + INTERVAL '1 day',  7, 1, 2, 12),
                                                                                                                                                                                                          ('OK-D2E3F4', 600.00,   2760.00,   30.00,  15.00, 15.00, 'ANNULE',     NOW() - INTERVAL '4 days',  NULL,                        NOW() - INTERVAL '1 day',  8, 2, 3, 13),
                                                                                                                                                                                                          ('OK-G5H6I7', 5500.00,  336600.00, 275.00, 137.50,137.50,'PAYE',       NOW() - INTERVAL '3 days',  NOW() - INTERVAL '3 days',  NOW() + INTERVAL '1 day',  9, 2, 1, 14),
                                                                                                                                                                                                          ('OK-J8K9L1', 1900.00,  8740.00,   95.00,  47.50, 47.50, 'PAYE',       NOW() - INTERVAL '3 days',  NOW() - INTERVAL '3 days',  NOW() + INTERVAL '1 day', 10, 3, 2, 15),
                                                                                                                                                                                                          ('OK-M2N3O4', 1300.00,  79560.00,  65.00,  32.50, 32.50, 'PAYE',       NOW() - INTERVAL '1 day',   NOW() - INTERVAL '1 day',   NOW() + INTERVAL '1 day',  5, 3, 1,  1),
                                                                                                                                                                                                          ('OK-P5Q6R7', 2400.00,  11040.00,  120.00, 60.00, 60.00, 'EN_ATTENTE', NOW() - INTERVAL '1 day',   NULL,                        NOW() + INTERVAL '4 days', 6, 1, 2,  2),
                                                                                                                                                                                                          ('OK-S8T9U1', 850.00,   52020.00,  42.50,  21.25, 21.25, 'PAYE',       NOW(),                       NOW(),                       NOW() + INTERVAL '1 day',  7, 1, 1,  3),
                                                                                                                                                                                                          ('OK-V2W3X4', 3100.00,  14260.00,  155.00, 77.50, 77.50, 'EN_ATTENTE', NOW(),                       NULL,                        NOW() + INTERVAL '5 days', 8, 2, 2,  4)
    ON CONFLICT (referencecode) DO NOTHING;