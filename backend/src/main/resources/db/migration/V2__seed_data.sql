-- ============================================
-- DEVISES
-- ============================================

INSERT INTO currencies (code, name, symbol, active) VALUES
('EUR', 'Euro', '€', true),
('USD', 'Dollar américain', '$', true),
('GBP', 'Livre sterling', '£', true),
('MAD', 'Dirham marocain', 'DH', true),
('XOF', 'Franc CFA', 'CFA', true),
('CAD', 'Dollar canadien', 'CA$', true),
('CHF', 'Franc suisse', 'CHF', true)
ON CONFLICT (code) DO NOTHING;


-- ============================================
-- CORRIDORS DE TRANSFERT
-- ============================================

-- France -> Maroc (EUR -> MAD)
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'France', 'Maroc', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'EUR'
AND d.code = 'MAD';

-- Espagne -> Maroc
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'Espagne', 'Maroc', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'EUR'
AND d.code = 'MAD';

-- Belgique -> Maroc
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'Belgique', 'Maroc', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'EUR'
AND d.code = 'MAD';

-- France -> Sénégal
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'France', 'Sénégal', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'EUR'
AND d.code = 'XOF';

-- France -> Côte d''Ivoire
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'France', 'Côte d''Ivoire', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'EUR'
AND d.code = 'XOF';

-- Belgique -> Mali
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'Belgique', 'Mali', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'EUR'
AND d.code = 'XOF';

-- France -> Royaume-Uni
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'France', 'Royaume-Uni', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'EUR'
AND d.code = 'GBP';

-- France -> Suisse
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'France', 'Suisse', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'EUR'
AND d.code = 'CHF';

-- Royaume-Uni -> France
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'Royaume-Uni', 'France', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'GBP'
AND d.code = 'EUR';

-- Royaume-Uni -> Maroc
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'Royaume-Uni', 'Maroc', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'GBP'
AND d.code = 'MAD';

-- États-Unis -> Maroc
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'États-Unis', 'Maroc', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'USD'
AND d.code = 'MAD';

-- États-Unis -> Sénégal
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'États-Unis', 'Sénégal', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'USD'
AND d.code = 'XOF';

-- États-Unis -> France
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'États-Unis', 'France', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'USD'
AND d.code = 'EUR';

-- Canada -> Maroc
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'Canada', 'Maroc', s.id, d.id, true
FROM currencies s, currencies d
WHERE s.code = 'CAD'
AND d.code = 'MAD';

-- Canada -> France
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'Canada', 'France', s.id, d.id, false
FROM currencies s, currencies d
WHERE s.code = 'CAD'
AND d.code = 'EUR';

-- Maroc -> France
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'Maroc', 'France', s.id, d.id, false
FROM currencies s, currencies d
WHERE s.code = 'MAD'
AND d.code = 'EUR';

-- Maroc -> Espagne
INSERT INTO transfer_corridors
(sourcecountry, destinationcountry, source_currency_id, destination_currency_id, active)
SELECT 'Maroc', 'Espagne', s.id, d.id, false
FROM currencies s, currencies d
WHERE s.code = 'MAD'
AND d.code = 'EUR';