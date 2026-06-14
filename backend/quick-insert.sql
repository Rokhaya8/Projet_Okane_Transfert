-- Quick test data insertion
-- Run with: psql -U postgres -d okane_transfer -f quick-insert.sql

-- Insert users
INSERT INTO users (id, user_type, fullName, email, password, phone, active, createdAt, role)
VALUES
  (100, 'AGENT', 'Agent Test Caisse', 'agent.caisse@test.okane', 'dev-password', '+212600100100', true, now(), 'ROLE_AGENT'),
  (101, 'MANAGER', 'Manager Test Agence', 'manager.agence@test.okane', 'dev-password', '+212600100101', true, now(), 'ROLE_MANAGER'),
  (102, 'CLIENT', 'Client Test Transfert', 'client.transfert@test.okane', 'dev-password', '+212600100102', true, now(), 'ROLE_CLIENT');

-- Insert agencies
INSERT INTO agencies (id, name, address, country, dailyLimit, active, createdAt, manager_id)
VALUES
  (100, 'Agence Test Casablanca', 'Boulevard Mohammed V, Casablanca', 'Maroc', 500000.00, true, now(), 101);

-- Insert beneficiaries
INSERT INTO beneficiaries (id, fullName, phone, country, identityNumber, watchlistFlag)
VALUES
  (100, 'Moussa Diallo', '+221771112233', 'Senegal', 'SN123456', false),
  (101, 'Fatou Sow', '+221776667788', 'Senegal', 'SN998877', false),
  (102, 'Aminata Diop', '+221770001122', 'Senegal', 'SN445566', false);

-- Insert transfers (pending)
INSERT INTO transfers (
  id, referenceCode, amountSent, amountReceived, fees, commissionAgency,
  commissionCentral, status, createdAt, expiryDate, agent_id, agency_id, client_id, beneficiary_id
)
VALUES
  (100, 'TESTPAY1', 1000.00, 65000.00, 25.00, 10.00, 15.00, 'EN_ATTENTE', now() - interval '3 hours', now() + interval '7 days', 100, 100, 102, 100),
  (102, 'TESTPAY2', 2500.00, 162500.00, 50.00, 20.00, 30.00, 'EN_ATTENTE', now() - interval '2 hours', now() + interval '7 days', 100, 100, 102, 102);

SELECT 'Data inserted successfully!' as message;
