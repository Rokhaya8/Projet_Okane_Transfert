-- Données de test — mot de passe pour tous : password123
INSERT INTO users (full_name, email, password, phone, active, role, created_at)
VALUES (
    'Admin Okane',
    'admin@okane.com',
    '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S',
    '+212600000001',
    TRUE,
    'ROLE_ADMIN',
    NOW()
) ON CONFLICT (email) DO NOTHING;

INSERT INTO users (full_name, email, password, phone, active, role, created_at)
VALUES (
    'Karim Manager',
    'manager@okane.com',
    '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S',
    '+212600000002',
    TRUE,
    'ROLE_MANAGER',
    NOW()
) ON CONFLICT (email) DO NOTHING;

INSERT INTO agencies (name, address, country, daily_limit, active, manager_id, created_at)
SELECT
    'Agence Casablanca Centre',
    '12 Bd Mohammed V, Casablanca',
    'Maroc',
    500000.00,
    TRUE,
    u.id,
    NOW()
FROM users u
WHERE u.email = 'manager@okane.com'
  AND NOT EXISTS (SELECT 1 FROM agencies WHERE manager_id = u.id);

INSERT INTO users (full_name, email, password, phone, active, role, agency_id, created_at)
SELECT
    'Agent Ahmed',
    'agent@okane.com',
    '$2a$10$uE3IRHTP2LLpzUIW.CJ.KuEvtPA5WRdHSf8JECAZJpxU0aldgeA0S',
    '+212600000003',
    TRUE,
    'ROLE_AGENT',
    a.id,
    NOW()
FROM agencies a
JOIN users m ON a.manager_id = m.id AND m.email = 'manager@okane.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'agent@okane.com');
