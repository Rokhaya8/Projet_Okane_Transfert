-- Migration SQL - Gestion de Caisse
-- Projet Okane Transfer

-- ============================================
-- Table: cash_operations
-- Description: Enregistre toutes les opérations de caisse
-- ============================================
CREATE TABLE IF NOT EXISTS cash_operations (
    id BIGSERIAL PRIMARY KEY,
    cash_drawer_id BIGINT NOT NULL REFERENCES cash_drawers(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('ENCAISSEMENT', 'DECAISSEMENT', 'CLOTURE')),
    amount DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    operation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transfer_id BIGINT REFERENCES transfers(id) ON DELETE SET NULL,
    description VARCHAR(500)
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_cash_ops_drawer_date ON cash_operations(cash_drawer_id, operation_date);
CREATE INDEX IF NOT EXISTS idx_cash_ops_type ON cash_operations(type);
CREATE INDEX IF NOT EXISTS idx_cash_ops_transfer ON cash_operations(transfer_id);

-- ============================================
-- Table: discrepancy_reports
-- Description: Signalements d'écarts de caisse
-- ============================================
CREATE TABLE IF NOT EXISTS discrepancy_reports (
    id BIGSERIAL PRIMARY KEY,
    agent_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    cash_drawer_id BIGINT NOT NULL REFERENCES cash_drawers(id) ON DELETE CASCADE,
    ecart_constate DECIMAL(15,2) NOT NULL,
    commentaire VARCHAR(500),
    report_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX IF NOT EXISTS idx_discrepancy_agent ON discrepancy_reports(agent_id);
CREATE INDEX IF NOT EXISTS idx_discrepancy_date ON discrepancy_reports(report_date);
CREATE INDEX IF NOT EXISTS idx_discrepancy_drawer ON discrepancy_reports(cash_drawer_id);

-- ============================================
-- Table: audit_logs
-- Description: Journal d'audit de toutes les actions sensibles
-- ============================================
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    details TEXT,
    ip_address VARCHAR(45) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_logs(entity_type, entity_id);

-- ============================================
-- Mise à jour de la table cash_drawers (si nécessaire)
-- ============================================
ALTER TABLE cash_drawers ADD COLUMN IF NOT EXISTS opening_time TIMESTAMP;
ALTER TABLE cash_drawers ADD COLUMN IF NOT EXISTS closing_time TIMESTAMP;

-- ============================================
-- Données de test (optionnel - décommenter si besoin)
-- ============================================

-- Créer un agent de test
-- INSERT INTO users (full_name, email, password, phone, role, active, created_at)
-- VALUES ('Agent Test', 'agent@okane.com', '$2a$10$...', '0600000001', 'ROLE_AGENT', true, CURRENT_TIMESTAMP)
-- ON CONFLICT (email) DO NOTHING;

-- Créer une agence de test
-- INSERT INTO agencies (name, address, country, daily_limit, active, created_at)
-- VALUES ('Agence Test', '123 Rue Test', 'Morocco', 100000.00, true, CURRENT_TIMESTAMP)
-- ON CONFLICT DO NOTHING;

-- Créer une caisse de test
-- INSERT INTO cash_drawers (agent_id, agency_id, balance, status, opening_time)
-- SELECT u.id, a.id, 10000.00, 'OPEN', CURRENT_TIMESTAMP
-- FROM users u, agencies a
-- WHERE u.email = 'agent@okane.com' AND a.name = 'Agence Test'
-- ON CONFLICT DO NOTHING;

-- ============================================
-- Vérifications
-- ============================================

-- Vérifier les tables créées
DO $$
DECLARE
    v_cash_ops_count INTEGER;
    v_discrepancy_count INTEGER;
    v_audit_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_cash_ops_count FROM information_schema.tables 
    WHERE table_name = 'cash_operations';
    
    SELECT COUNT(*) INTO v_discrepancy_count FROM information_schema.tables 
    WHERE table_name = 'discrepancy_reports';
    
    SELECT COUNT(*) INTO v_audit_count FROM information_schema.tables 
    WHERE table_name = 'audit_logs';
    
    IF v_cash_ops_count > 0 AND v_discrepancy_count > 0 AND v_audit_count > 0 THEN
        RAISE NOTICE '✅ Migration complétée avec succès !';
        RAISE NOTICE '   - cash_operations: OK';
        RAISE NOTICE '   - discrepancy_reports: OK';
        RAISE NOTICE '   - audit_logs: OK';
    ELSE
        RAISE EXCEPTION '❌ Erreur lors de la création des tables';
    END IF;
END $$;

-- ============================================
-- Commentaires sur les tables
-- ============================================
COMMENT ON TABLE cash_operations IS 'Opérations de caisse (encaissements, décaissements, clôtures)';
COMMENT ON TABLE discrepancy_reports IS 'Signalements d''écarts de caisse';
COMMENT ON TABLE audit_logs IS 'Journal d''audit de toutes les actions sensibles';

COMMENT ON COLUMN cash_operations.type IS 'Type: ENCAISSEMENT, DECAISSEMENT, CLOTURE';
COMMENT ON COLUMN cash_operations.balance_after IS 'Solde de la caisse après l''opération';
COMMENT ON COLUMN discrepancy_reports.ecart_constate IS 'Écart constaté (peut être négatif)';

-- ============================================
-- FIN DE LA MIGRATION
-- ============================================
