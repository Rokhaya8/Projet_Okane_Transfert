-- Migration pour les nouvelles fonctionnalités Agent
-- Ajoute les colonnes manquantes si elles n'existent pas déjà

-- Table transfers: s'assurer que les colonnes pour le paiement existent
ALTER TABLE transfers ADD COLUMN IF NOT EXISTS paying_agent_id BIGINT REFERENCES users(id);
ALTER TABLE transfers ADD COLUMN IF NOT EXISTS beneficiary_id_doc VARCHAR(500);
ALTER TABLE transfers ADD COLUMN IF NOT EXISTS paid_at TIMESTAMP;
ALTER TABLE transfers ADD COLUMN IF NOT EXISTS expiry_date TIMESTAMP;

-- Calculer expiry_date pour les transferts existants sans date d'expiration
UPDATE transfers 
SET expiry_date = created_at + INTERVAL '30 days'
WHERE expiry_date IS NULL;

-- Index pour améliorer les performances des recherches
CREATE INDEX IF NOT EXISTS idx_transfers_reference_code ON transfers(reference_code);
CREATE INDEX IF NOT EXISTS idx_transfers_status ON transfers(status);
CREATE INDEX IF NOT EXISTS idx_transfers_created_at ON transfers(created_at);
CREATE INDEX IF NOT EXISTS idx_transfers_paying_agent ON transfers(paying_agent_id);

-- Table cash_operations: s'assurer que les colonnes existent
CREATE TABLE IF NOT EXISTS cash_operations (
    id BIGSERIAL PRIMARY KEY,
    cash_drawer_id BIGINT NOT NULL REFERENCES cash_drawers(id),
    type VARCHAR(20) NOT NULL CHECK (type IN ('ENCAISSEMENT', 'DECAISSEMENT', 'CLOTURE')),
    amount DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    operation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transfer_id BIGINT REFERENCES transfers(id),
    description VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_cash_ops_drawer_date ON cash_operations(cash_drawer_id, operation_date);
CREATE INDEX IF NOT EXISTS idx_cash_ops_type ON cash_operations(type);

-- Table discrepancy_reports: s'assurer qu'elle existe
CREATE TABLE IF NOT EXISTS discrepancy_reports (
    id BIGSERIAL PRIMARY KEY,
    agent_id BIGINT NOT NULL REFERENCES users(id),
    cash_drawer_id BIGINT NOT NULL REFERENCES cash_drawers(id),
    ecart_constate DECIMAL(15,2) NOT NULL,
    commentaire VARCHAR(500),
    report_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_discrepancy_agent ON discrepancy_reports(agent_id);
CREATE INDEX IF NOT EXISTS idx_discrepancy_date ON discrepancy_reports(report_date);

-- Table audit_logs: s'assurer qu'elle existe
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    details TEXT,
    ip_address VARCHAR(45) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_logs(entity_type, entity_id);

-- Table beneficiaries: ajouter phone si manquant
ALTER TABLE beneficiaries ADD COLUMN IF NOT EXISTS phone VARCHAR(20);

-- Table cash_drawers: s'assurer des colonnes de gestion
ALTER TABLE cash_drawers ADD COLUMN IF NOT EXISTS opening_time TIMESTAMP;
ALTER TABLE cash_drawers ADD COLUMN IF NOT EXISTS closing_time TIMESTAMP;

-- Commentaires sur les tables
COMMENT ON TABLE cash_operations IS 'Opérations de caisse (encaissements, décaissements, clôtures)';
COMMENT ON TABLE discrepancy_reports IS 'Signalements d''écarts de caisse';
COMMENT ON TABLE audit_logs IS 'Journal d''audit de toutes les actions sensibles';

-- Vérification de l'intégrité
DO $$
BEGIN
    RAISE NOTICE 'Migration complétée avec succès';
    RAISE NOTICE 'Tables créées/vérifiées: cash_operations, discrepancy_reports, audit_logs';
    RAISE NOTICE 'Colonnes ajoutées à transfers: paying_agent_id, beneficiary_id_doc, paid_at, expiry_date';
    RAISE NOTICE 'Index créés pour optimiser les performances';
END $$;
