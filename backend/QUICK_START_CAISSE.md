# 🚀 Démarrage Rapide - Gestion de Caisse

## ⚡ Installation en 5 minutes

### 1. Créer les tables (SQL)
```sql
-- Exécuter dans PostgreSQL
CREATE TABLE IF NOT EXISTS cash_operations (
    id BIGSERIAL PRIMARY KEY,
    cash_drawer_id BIGINT NOT NULL REFERENCES cash_drawers(id),
    type VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    operation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transfer_id BIGINT REFERENCES transfers(id),
    description VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS discrepancy_reports (
    id BIGSERIAL PRIMARY KEY,
    agent_id BIGINT NOT NULL REFERENCES users(id),
    cash_drawer_id BIGINT NOT NULL REFERENCES cash_drawers(id),
    ecart_constate DECIMAL(15,2) NOT NULL,
    commentaire VARCHAR(500),
    report_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

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
```

### 2. Créer une caisse de test
```sql
INSERT INTO cash_drawers (agent_id, agency_id, balance, status, opening_time)
VALUES (1, 1, 10000.00, 'OPEN', CURRENT_TIMESTAMP);
```

### 3. Compiler et tester
```bash
cd e:\Projet_Okane_Transfert\backend
mvn clean install
mvn test
```

### 4. Déployer
```bash
mvn package
# Copier target/okane-transfer-1.0-SNAPSHOT.war vers votre serveur
```

## 🧪 Test Complet (avec cURL)

```bash
# 1. Login et récupérer le token
TOKEN=$(curl -s -X POST http://localhost:8080/okane/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"agent@okane.com","password":"password123"}' \
  | jq -r '.token')

echo "Token: $TOKEN"

# 2. Consulter le solde
curl -X GET http://localhost:8080/okane/api/agent/cash/balance \
  -H "Authorization: Bearer $TOKEN" | jq

# 3. Voir les opérations du jour
curl -X GET http://localhost:8080/okane/api/agent/cash/operations \
  -H "Authorization: Bearer $TOKEN" | jq

# 4. Clôturer la caisse
curl -X POST http://localhost:8080/okane/api/agent/cash/close \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"soldeReelSaisi": 10000.00}' | jq
```

## 📂 Structure des Fichiers Essentiels

```
backend/
├── src/main/java/com/okanetransfer/
│   ├── controller/
│   │   └── AgentCashController.java      ⭐ Endpoints REST
│   ├── service/
│   │   ├── CashService.java              ⭐ Logique métier
│   │   └── AuditService.java             ⭐ Audit
│   ├── dto/
│   │   ├── CashBalanceDTO.java
│   │   ├── OperationCaisseDTO.java
│   │   ├── CashCloseRequestDTO.java
│   │   ├── CashCloseResponseDTO.java
│   │   └── DiscrepancyRequestDTO.java
│   └── exception/
│       ├── CashDrawerAlreadyClosedException.java
│       └── GlobalExceptionHandler.java
└── src/test/java/com/okanetransfer/
    ├── service/CashServiceTest.java      ⭐ Tests service
    └── controller/AgentCashControllerTest.java ⭐ Tests controller
```

## 🎯 Endpoints Disponibles

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/agent/cash/balance` | Solde actuel |
| GET | `/api/agent/cash/operations` | Opérations du jour |
| POST | `/api/agent/cash/close` | Clôture journalière |
| POST | `/api/agent/cash/discrepancy` | Signaler écart |

## ✅ Vérification

### Vérifier les tests
```bash
mvn test -Dtest=CashServiceTest
mvn test -Dtest=AgentCashControllerTest
```

Résultat attendu: **16 tests passent** ✅

### Vérifier la BD
```sql
-- Voir les opérations
SELECT * FROM cash_operations ORDER BY operation_date DESC LIMIT 10;

-- Voir les écarts signalés
SELECT * FROM discrepancy_reports ORDER BY report_date DESC LIMIT 10;

-- Voir l'audit
SELECT * FROM audit_logs WHERE action LIKE '%CASH%' ORDER BY timestamp DESC LIMIT 10;
```

## 🔧 Configuration Minimale

### application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/okane_db
spring.datasource.username=okane_user
spring.datasource.password=your_password

jwt.secret=YourSecretKey
```

## 🐛 Problèmes Courants

### "Aucune caisse trouvée"
➜ Créer une caisse pour l'agent (voir SQL ci-dessus)

### "Caisse déjà clôturée"
➜ Normal, une seule clôture/jour autorisée

### Tests échouent
```bash
mvn clean test
```

## 📱 Test avec Postman

1. Importer la collection (dans Postman):
   - POST `/api/auth/login`
   - GET `/api/agent/cash/balance`
   - GET `/api/agent/cash/operations`
   - POST `/api/agent/cash/close`
   - POST `/api/agent/cash/discrepancy`

2. Configurer variable `token` après login

3. Exécuter les requêtes dans l'ordre

## 🎓 Prêt !

Votre backend de gestion de caisse est maintenant opérationnel ! 🎉

**Prochaines étapes:**
- [ ] Tester tous les endpoints
- [ ] Vérifier les logs d'audit
- [ ] Intégrer avec le frontend
- [ ] Déployer en production

---

**Support:** Voir GESTION_CAISSE.md pour plus de détails
