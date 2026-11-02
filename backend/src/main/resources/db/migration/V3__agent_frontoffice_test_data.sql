-- Test data for the agent front-office screens.
-- Use this while authentication is not wired yet.
--
-- Frontend test context:
--   agentId = 100
--   agencyId = 100
--
-- Searchable pending transfers:
--   Code: TESTPAY1 / Phone: +221771112233
--   Code: TESTPAY2 / Phone: +221770001122
--
-- Existing paid transfer for history:
--   Code: TESTPAID

INSERT INTO users (id, user_type, fullName, email, password, phone, active, createdAt, lastLogin, role)
VALUES
  (100, 'AGENT', 'Agent Test Caisse', 'agent.caisse@test.okane', 'dev-password', '+212600100100', true, now(), null, 'ROLE_AGENT'),
  (101, 'MANAGER', 'Manager Test Agence', 'manager.agence@test.okane', 'dev-password', '+212600100101', true, now(), null, 'ROLE_MANAGER'),
  (102, 'CLIENT', 'Client Test Transfert', 'client.transfert@test.okane', 'dev-password', '+212600100102', true, now(), null, 'ROLE_CLIENT')
ON CONFLICT (id) DO UPDATE SET
  user_type = EXCLUDED.user_type,
  fullName = EXCLUDED.fullName,
  email = EXCLUDED.email,
  phone = EXCLUDED.phone,
  active = EXCLUDED.active,
  role = EXCLUDED.role;

INSERT INTO agencies (id, name, address, country, dailyLimit, active, createdAt, manager_id)
VALUES
  (100, 'Agence Test Casablanca', 'Boulevard Mohammed V, Casablanca', 'Maroc', 500000.00, true, now(), 101)
ON CONFLICT (id) DO UPDATE SET
  name = EXCLUDED.name,
  address = EXCLUDED.address,
  country = EXCLUDED.country,
  dailyLimit = EXCLUDED.dailyLimit,
  active = EXCLUDED.active,
  manager_id = EXCLUDED.manager_id;

INSERT INTO beneficiaries (id, fullName, phone, country, identityNumber, watchlistFlag)
VALUES
  (100, 'Moussa Diallo', '+221771112233', 'Senegal', 'SN123456', false),
  (101, 'Fatou Sow', '+221776667788', 'Senegal', 'SN998877', false),
  (102, 'Aminata Diop', '+221770001122', 'Senegal', 'SN445566', false)
ON CONFLICT (id) DO UPDATE SET
  fullName = EXCLUDED.fullName,
  phone = EXCLUDED.phone,
  country = EXCLUDED.country,
  identityNumber = EXCLUDED.identityNumber,
  watchlistFlag = EXCLUDED.watchlistFlag;

-- Closed session: gives visible caisse history without preventing "Ouvrir la caisse".
UPDATE agent_cash_sessions
SET status = 'CLOSED',
    closedAt = COALESCE(closedAt, now()),
    closingBalance = COALESCE(closingBalance, currentBalance),
    countedAmount = COALESCE(countedAmount, currentBalance),
    discrepancyAmount = COALESCE(discrepancyAmount, 0.00)
WHERE agent_id = 100
  AND status = 'OPEN';

INSERT INTO agent_cash_sessions (
  id, agent_id, agency_id, openingBalance, currentBalance, closingBalance,
  countedAmount, discrepancyAmount, openedAt, closedAt, status
)
VALUES
  (100, 100, 100, 10000.00, 17500.00, 17500.00, 17500.00, 0.00, now() - interval '1 day', now() - interval '23 hours', 'CLOSED')
ON CONFLICT (id) DO UPDATE SET
  agent_id = EXCLUDED.agent_id,
  agency_id = EXCLUDED.agency_id,
  openingBalance = EXCLUDED.openingBalance,
  currentBalance = EXCLUDED.currentBalance,
  closingBalance = EXCLUDED.closingBalance,
  countedAmount = EXCLUDED.countedAmount,
  discrepancyAmount = EXCLUDED.discrepancyAmount,
  openedAt = EXCLUDED.openedAt,
  closedAt = EXCLUDED.closedAt,
  status = EXCLUDED.status;

INSERT INTO transfers (
  id, referenceCode, amountSent, amountReceived, fees, commissionAgency,
  commissionCentral, commission_agency, commission_central, status, createdAt, paidAt, expiryDate, agent_id,
  paying_agent_id, agency_id, paying_agency_id, corridor_id, client_id, beneficiary_id
)
VALUES
  (100, 'TESTPAY1', 1000.00, 65000.00, 25.00, 10.00, 15.00, 10.00, 15.00, 'EN_ATTENTE', now() - interval '3 hours', null, now() + interval '7 days', 100, null, 100, null, null, 102, 100),
  (101, 'TESTPAID', 500.00, 32500.00, 15.00, 6.00, 9.00, 6.00, 9.00, 'PAYE', now() - interval '1 day', now() - interval '23 hours 20 minutes', now() + interval '6 days', 100, 100, 100, 100, null, 102, 101),
  (102, 'TESTPAY2', 2500.00, 162500.00, 50.00, 20.00, 30.00, 20.00, 30.00, 'EN_ATTENTE', now() - interval '2 hours', null, now() + interval '7 days', 100, null, 100, null, null, 102, 102)
ON CONFLICT (id) DO UPDATE SET
  referenceCode = EXCLUDED.referenceCode,
  amountSent = EXCLUDED.amountSent,
  amountReceived = EXCLUDED.amountReceived,
  fees = EXCLUDED.fees,
  commissionAgency = EXCLUDED.commissionAgency,
  commissionCentral = EXCLUDED.commissionCentral,
  commission_agency = EXCLUDED.commission_agency,
  commission_central = EXCLUDED.commission_central,
  status = EXCLUDED.status,
  createdAt = EXCLUDED.createdAt,
  paidAt = EXCLUDED.paidAt,
  expiryDate = EXCLUDED.expiryDate,
  agent_id = EXCLUDED.agent_id,
  paying_agent_id = EXCLUDED.paying_agent_id,
  agency_id = EXCLUDED.agency_id,
  paying_agency_id = EXCLUDED.paying_agency_id,
  corridor_id = EXCLUDED.corridor_id,
  client_id = EXCLUDED.client_id,
  beneficiary_id = EXCLUDED.beneficiary_id;

INSERT INTO transfer_payments (
  id, transfer_id, agent_id, agency_id, beneficiaryIdentityNumber,
  paidAmount, paidAt, receiptNumber
)
VALUES
  (100, 101, 100, 100, 'SN998877', 32500.00, now() - interval '23 hours 20 minutes', 'RCT-TEST-PAID')
ON CONFLICT (id) DO UPDATE SET
  transfer_id = EXCLUDED.transfer_id,
  agent_id = EXCLUDED.agent_id,
  agency_id = EXCLUDED.agency_id,
  beneficiaryIdentityNumber = EXCLUDED.beneficiaryIdentityNumber,
  paidAmount = EXCLUDED.paidAmount,
  paidAt = EXCLUDED.paidAt,
  receiptNumber = EXCLUDED.receiptNumber;

INSERT INTO cash_operations (
  id, cash_session_id, operationType, amount, balanceBefore, balanceAfter,
  operationDate, reference, transfer_id
)
VALUES
  (100, 100, 'OPENING', 10000.00, 0.00, 10000.00, now() - interval '1 day', 'OPEN-100', null),
  (101, 100, 'ADJUSTMENT', 40000.00, 10000.00, 50000.00, now() - interval '23 hours 45 minutes', 'Alimentation caisse test', null),
  (102, 100, 'TRANSFER_PAID', 32500.00, 50000.00, 17500.00, now() - interval '23 hours 20 minutes', 'TESTPAID', 101),
  (103, 100, 'CLOSING', 17500.00, 17500.00, 17500.00, now() - interval '23 hours', 'CLOSE-100', null)
ON CONFLICT (id) DO UPDATE SET
  cash_session_id = EXCLUDED.cash_session_id,
  operationType = EXCLUDED.operationType,
  amount = EXCLUDED.amount,
  balanceBefore = EXCLUDED.balanceBefore,
  balanceAfter = EXCLUDED.balanceAfter,
  operationDate = EXCLUDED.operationDate,
  reference = EXCLUDED.reference,
  transfer_id = EXCLUDED.transfer_id;

SELECT setval(pg_get_serial_sequence('users', 'id'), GREATEST((SELECT max(id) FROM users), 102), true);
SELECT setval(pg_get_serial_sequence('agencies', 'id'), GREATEST((SELECT max(id) FROM agencies), 100), true);
SELECT setval(pg_get_serial_sequence('beneficiaries', 'id'), GREATEST((SELECT max(id) FROM beneficiaries), 102), true);
SELECT setval(pg_get_serial_sequence('agent_cash_sessions', 'id'), GREATEST((SELECT max(id) FROM agent_cash_sessions), 100), true);
SELECT setval(pg_get_serial_sequence('transfers', 'id'), GREATEST((SELECT max(id) FROM transfers), 102), true);
SELECT setval(pg_get_serial_sequence('transfer_payments', 'id'), GREATEST((SELECT max(id) FROM transfer_payments), 100), true);
SELECT setval(pg_get_serial_sequence('cash_operations', 'id'), GREATEST((SELECT max(id) FROM cash_operations), 103), true);
