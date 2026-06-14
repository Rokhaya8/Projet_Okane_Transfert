-- Dev seed data for the agent frontend.
-- Execute this script on the okane_transfer PostgreSQL database after the schema exists.

INSERT INTO users (id, user_type, fullName, email, password, phone, active, createdAt, lastLogin, role)
VALUES
  (1, 'AGENT', 'Agent Okane', 'agent@okane.test', 'dev-password', '+212600000001', true, now(), null, 'ROLE_AGENT'),
  (2, 'MANAGER', 'Manager Okane', 'manager@okane.test', 'dev-password', '+212600000002', true, now(), null, 'ROLE_MANAGER'),
  (3, 'CLIENT', 'Client Demo', 'client@okane.test', 'dev-password', '+212600000003', true, now(), null, 'ROLE_CLIENT')
ON CONFLICT (id) DO UPDATE SET
  user_type = EXCLUDED.user_type,
  fullName = EXCLUDED.fullName,
  email = EXCLUDED.email,
  phone = EXCLUDED.phone,
  active = EXCLUDED.active,
  role = EXCLUDED.role;

INSERT INTO agencies (id, name, address, country, dailyLimit, active, createdAt, manager_id)
VALUES
  (1, 'Agence Principale', 'Boulevard Mohammed V, Casablanca', 'Maroc', 500000.00, true, now(), 2)
ON CONFLICT (id) DO UPDATE SET
  name = EXCLUDED.name,
  address = EXCLUDED.address,
  country = EXCLUDED.country,
  dailyLimit = EXCLUDED.dailyLimit,
  active = EXCLUDED.active,
  manager_id = EXCLUDED.manager_id;

INSERT INTO beneficiaries (id, fullName, phone, country, identityNumber, watchlistFlag)
VALUES
  (1, 'Moussa Diallo', '+221771112233', 'Senegal', 'SN123456', false),
  (2, 'Fatou Sow', '+221776667788', 'Senegal', 'SN998877', false),
  (3, 'Aminata Diop', '+221770001122', 'Senegal', 'SN445566', false)
ON CONFLICT (id) DO UPDATE SET
  fullName = EXCLUDED.fullName,
  phone = EXCLUDED.phone,
  country = EXCLUDED.country,
  identityNumber = EXCLUDED.identityNumber,
  watchlistFlag = EXCLUDED.watchlistFlag;

INSERT INTO agent_cash_sessions (
  id, agent_id, agency_id, openingBalance, currentBalance, closingBalance,
  countedAmount, discrepancyAmount, openedAt, closedAt, status
)
VALUES
  (1, 1, 1, 10000.00, 15600.00, null, null, null, now() - interval '2 hours', null, 'OPEN')
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
  commissionCentral, status, createdAt, paidAt, expiryDate, agent_id,
  paying_agent_id, agency_id, paying_agency_id, corridor_id, client_id, beneficiary_id
)
VALUES
  (1, 'OKN20261', 1000.00, 65000.00, 25.00, 10.00, 15.00, 'EN_ATTENTE', now() - interval '1 day', null, now() + interval '6 days', 1, null, 1, null, null, 3, 1),
  (2, 'OKN20262', 500.00, 32500.00, 15.00, 6.00, 9.00, 'PAYE', now() - interval '2 days', now() - interval '1 hour', now() + interval '5 days', 1, 1, 1, 1, null, 3, 2),
  (3, 'OKN20263', 2500.00, 162500.00, 50.00, 20.00, 30.00, 'EN_ATTENTE', now() - interval '3 hours', null, now() + interval '7 days', 1, null, 1, null, null, 3, 3)
ON CONFLICT (id) DO UPDATE SET
  referenceCode = EXCLUDED.referenceCode,
  amountSent = EXCLUDED.amountSent,
  amountReceived = EXCLUDED.amountReceived,
  fees = EXCLUDED.fees,
  commissionAgency = EXCLUDED.commissionAgency,
  commissionCentral = EXCLUDED.commissionCentral,
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
  (1, 2, 1, 1, 'SN998877', 32500.00, now() - interval '1 hour', 'RCT-DEMO-001')
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
  (1, 1, 'OPENING', 10000.00, 0.00, 10000.00, now() - interval '2 hours', 'OPEN-1', null),
  (2, 1, 'ADJUSTMENT', 40000.00, 10000.00, 50000.00, now() - interval '90 minutes', 'Alimentation caisse', null),
  (3, 1, 'TRANSFER_PAID', 32500.00, 50000.00, 17500.00, now() - interval '1 hour', 'OKN20262', 2),
  (4, 1, 'ADJUSTMENT', -1900.00, 17500.00, 15600.00, now() - interval '20 minutes', 'Correction caisse', null)
ON CONFLICT (id) DO UPDATE SET
  cash_session_id = EXCLUDED.cash_session_id,
  operationType = EXCLUDED.operationType,
  amount = EXCLUDED.amount,
  balanceBefore = EXCLUDED.balanceBefore,
  balanceAfter = EXCLUDED.balanceAfter,
  operationDate = EXCLUDED.operationDate,
  reference = EXCLUDED.reference,
  transfer_id = EXCLUDED.transfer_id;

SELECT setval(pg_get_serial_sequence('users', 'id'), 3, true);
SELECT setval(pg_get_serial_sequence('agencies', 'id'), 1, true);
SELECT setval(pg_get_serial_sequence('beneficiaries', 'id'), 3, true);
SELECT setval(pg_get_serial_sequence('agent_cash_sessions', 'id'), 1, true);
SELECT setval(pg_get_serial_sequence('transfers', 'id'), 3, true);
SELECT setval(pg_get_serial_sequence('transfer_payments', 'id'), 1, true);
SELECT setval(pg_get_serial_sequence('cash_operations', 'id'), 4, true);
